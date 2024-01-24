package customer.odata_experments.handlers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sap.cds.ql.cqn.CqnPlain;
import com.sap.cds.ql.cqn.CqnToken;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.CqnVisitor;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.Books;

@Component
@ServiceName(CatalogService_.CDS_NAME)
public class CatalogServiceHandler implements EventHandler {

	@Before(event = CqnService.EVENT_READ)
	public void beforeHandler(CdsReadEventContext context) {
		Optional<CqnPredicate> whereClauseOptional = context.getCqn().where();
		if (whereClauseOptional.isPresent()) {
			List<CqnToken> tokens = whereClauseOptional.get().tokens().toList();

			if (containsLtOrGt(tokens)) {
				throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Server does not accept filters including LT or GT");
			}
		}
	}

	private boolean containsLtOrGt(List<CqnToken> tokens) {
		return tokens.stream()
				.filter(token -> token instanceof CqnPlain)
				.map(token -> (CqnPlain) token)
				.map(CqnPlain::plain)
				.anyMatch(token -> token.equals("<") || token.equals(">"));
	}

	@After(event = CqnService.EVENT_READ)
	public void discountBooks(Stream<Books> books) {
		books.filter(b -> b.getTitle() != null && b.getStock() != null)
		.filter(b -> b.getStock() > 200)
		.forEach(b -> b.setTitle(b.getTitle() + " (discounted)"));
	}

}
