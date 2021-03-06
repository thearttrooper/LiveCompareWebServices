import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import com.intellicorp.schemas.livecompare.*;

public class LiveCompareWebServiceClient {
    public static void main(String[] args) throws Exception {
	GetTableContents service = new GetTableContents();
	GetTableContentsSoap soap = service.getGetTableContentsSoap();
	AsynchronousStartResult start_result = soap.startWorkflow("T001");
	String token = start_result.getToken();
	int interval = start_result.getInterval();
	Status status = Status.UNRECOGNIZED_TOKEN;
	AsynchronousEndResult end_result = soap.endWorkflow(token);

	while (true) {
	    status = end_result.getStatus();

	    System.out.println(status.name());

	    if (status == Status.FAILED ||
		status == Status.COMPLETED ||
		status == Status.UNRECOGNIZED_TOKEN) {
		break;
	    }

	    System.out.format("Sleeping for %ds...\n", interval);
	    Thread.sleep(interval * 1000);

	    // NEVER call endWorkflow() unless in QUEUED or RUNNING
	    // state. If you call it, for example, once COMPLETED you
	    // will lose access to the result.
	    end_result = soap.endWorkflow(token);
	}

	if (status == Status.COMPLETED) {
	    WorkflowResult result = end_result.getWorkflowResult();
	    Table table = result.getResult();
	    TableHead head = table.getHead();
	    TableBody body = table.getBody();
	    boolean first = true;

	    for (TableHeadField field : head.getField()) {
		if (!first)
		    System.out.print(",");
		
		System.out.print(field.getName());

		first = false;
	    }

	    System.out.println();

	    for (TableRow row : body.getRow()) {
		first = true;

		for (String cell : row.getCell()) {
		    if (!first)
			System.out.print(",");

		    System.out.print(cell);
		    
		    first = false;
		}

		System.out.println();
	    }
	}
    }
}
