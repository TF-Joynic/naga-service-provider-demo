package indi.joynic.listener;

import indi.joynic.naga.lib.utils.SocketAddrUtil;
import indi.joynic.naga.portal.server.serviceprovider.register.RegisterOnServerSubjectWithThrift;
import indi.joynic.naga.portal.server.serviceprovider.service.ThriftNamingServerPortal;
import indi.joynic.naga.rpc.client.thrift.ThriftRpcClient;
import indi.joynic.naga.rpc.client.thrift.impl.ThriftRpcClientBuilder;
import indi.joynic.naga.rpc.config.ThriftRpcConfig;
import indi.joynic.naga.rpc.config.enums.RpcMode;
import indi.joynic.naga.serviceprovider.listener.NamingRegisterAtIntervalsListener;
import indi.joynic.naga.serviceprovider.portal.server.client.ThriftNamingServerPortalClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class ListenerConfig {
    private static final Logger logger = LoggerFactory.getLogger(ListenerConfig.class);

    @Value("${application.naga.naming.server.host}")
    private String namingServerHost;

    @Value("${application.naga.naming.server.port}")
    private Integer namingServerPort;

    @Value("${application.naga.naming.server.timeout}")
    private Integer namingServerTimeoutMillis;

    @Value("${application.service.namespace}")
    private String namespace;

    @Value("${application.service.service_name}")
    private String serviceName;

    @Value("${application.service.protocol_type}")
    private String protocolType;

    @Value("${application.service.weight}")
    private Integer weight;

    @Value("${application.service.port}")
    private Integer port;

    @Value("${application.service.register_interval}")
    private Long registerInterval;

    @Bean
    public ThriftRpcClient<ThriftNamingServerPortal.Client> thriftNamingServerRpcClient() {

        ThriftRpcConfig thriftRpcConfig = new ThriftRpcConfig();

        thriftRpcConfig.setRpcMode(RpcMode.THRIFT);
        thriftRpcConfig.setClientClazz(ThriftNamingServerPortal.Client.class);
        thriftRpcConfig.setProtocolClazz(TBinaryProtocol.class);
        thriftRpcConfig.setTransportClazz(TSocket.class);
        thriftRpcConfig.setHost(namingServerHost);
        thriftRpcConfig.setTimeoutMillis(namingServerTimeoutMillis);
        thriftRpcConfig.setPort(namingServerPort);

        ThriftRpcClientBuilder<ThriftNamingServerPortal.Client> builder
                = new ThriftRpcClientBuilder<>(thriftRpcConfig);

        ThriftRpcClient<ThriftNamingServerPortal.Client> thriftNamingServerRpcClient = null;
        try {
            thriftNamingServerRpcClient = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thriftNamingServerRpcClient;
    }

    @EventListener
    public void namingDoRegisterListener(ApplicationReadyEvent applicationReadyEvent) {
        ThriftRpcClient<ThriftNamingServerPortal.Client> thriftNamingServerRpcClient = thriftNamingServerRpcClient();

        RegisterOnServerSubjectWithThrift.AccessArgs accessArgs
                = NamingRegisterAtIntervalsListener.buildAccessArgs(namespace, protocolType, serviceName, port, weight);

        accessArgs.setHost(SocketAddrUtil.getIntranetIp());

        ThriftNamingServerPortalClient thriftNamingServerPortalClient
                = new ThriftNamingServerPortalClient(thriftNamingServerRpcClient);

        NamingRegisterAtIntervalsListener listener
                = new NamingRegisterAtIntervalsListener(thriftNamingServerPortalClient, accessArgs, registerInterval);

        listener.onApplicationEvent(applicationReadyEvent);
        logger.info("listner: {}", accessArgs.getPort());
    }
}
