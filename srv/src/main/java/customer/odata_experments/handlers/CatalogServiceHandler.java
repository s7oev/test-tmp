package customer.odata_experments.handlers;

import java.util.Optional;
import java.util.stream.Stream;

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
		CdsModel model = context.getModel();
		CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(model);
		
		Optional<CqnPredicate> whereClauseOptional = context.getCqn().where();
		if (whereClauseOptional.isPresent()) {
			CqnPredicate whereClause = whereClauseOptional.get();
			var a = 5;
			// whereClause.
		}
	}

	@After(event = CqnService.EVENT_READ)
	public void discountBooks(Stream<Books> books) {
		books.filter(b -> b.getTitle() != null && b.getStock() != null)
		.filter(b -> b.getStock() > 200)
		.forEach(b -> b.setTitle(b.getTitle() + " (discounted)"));
	}

}