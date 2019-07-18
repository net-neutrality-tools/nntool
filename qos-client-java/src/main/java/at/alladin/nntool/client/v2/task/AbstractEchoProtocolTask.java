package at.alladin.nntool.client.v2.task;

import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.v2.task.result.QoSTestResultEnum;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public abstract class AbstractEchoProtocolTask extends AbstractQoSTask {

    public final static String RESULT_PROTOCOL = "echo_protocol_objective_protocol";

    public final static String PROTOCOL_TCP = "tcp";

    public final static String PROTOCOL_UDP = "udp";

    public final static String PARAM_PAYLOAD = "echo_protocol_objective_payload";

    public final static String RESULT_PORT = "echo_protocol_objective_port";

    protected final static String RESULT = "echo_protocol_result";

    protected final static String RESULT_TIMEOUT = "echo_protocol_objective_timeout";

    protected final static String RESULT_STATUS = "echo_protocol_status";

    protected final static String RESULT_RTT_NS = "echo_protocol_result_rtt_ns";

    public final static String PARAM_TIMEOUT = "timeout";

    //the port to test against
    public final static String PARAM_SERVER_PORT = "echo_protocol_objective_server_port";

    //the server to test against
    public final static String PARAM_SERVER_ADDRESS = "echo_protocol_objective_server_addr";

    protected final static long DEFAULT_TIMEOUT = 3000000000L;

    protected String payload;

    protected long timeout;

    protected Integer testPort;

    protected String testHost;

    public AbstractEchoProtocolTask (final QualityOfServiceTest nnTest, final TaskDesc taskDesc, final int threadId, final int id) {
        super(nnTest, taskDesc, threadId, id);

        String value = (String) taskDesc.getParams().get(PARAM_SERVER_PORT);
        this.testPort = value != null ? Integer.valueOf(value) : null;

        value = (String) taskDesc.getParams().get(PARAM_TIMEOUT);
        this.timeout = value != null ? Long.valueOf(value) : DEFAULT_TIMEOUT;

        value = (String) taskDesc.getParams().get(PARAM_PAYLOAD);
        this.payload = value != null ? value : "default_payload";

        this.testHost = (String) taskDesc.getParams().get(PARAM_SERVER_ADDRESS);
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.QoSTask#getTestType()
     */
    public QoSTestResultEnum getTestType() {
        return QoSTestResultEnum.ECHO_PROTOCOL;
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.QoSTask#needsQoSControlConnection()
     */
    public boolean needsQoSControlConnection() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.AbstractRmbtTask#initTask()
     */
    @Override
    public void initTask() {
    }

}
