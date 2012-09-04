package org.dna.mqtt.moquette.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.dna.mqtt.moquette.messaging.spi.IMessaging;
import org.dna.mqtt.moquette.messaging.spi.INotifier;
import org.dna.mqtt.moquette.messaging.spi.impl.events.NotifyEvent;
import org.dna.mqtt.moquette.messaging.spi.impl.events.PubAckEvent;
import org.dna.mqtt.moquette.proto.messages.*;

import static org.dna.mqtt.moquette.proto.messages.AbstractMessage.*;
import org.dna.mqtt.moquette.proto.messages.AbstractMessage.QOSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MINA MQTT Handler used to route messages to protocol logic
 *
 * @author andrea
 */
public class MQTTHandler extends IoHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MQTTHandler.class);
    private IMessaging m_messaging;

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        AbstractMessage msg = (AbstractMessage) message;
        LOG.info("Received a message of type {0}", msg.getMessageType());
        try {
            switch (msg.getMessageType()) {
                case CONNECT:
                    handleConnect(session, (ConnectMessage) msg);
                    break;
                case SUBSCRIBE:
                    handleSubscribe(session, (SubscribeMessage) msg);
                    break;
                case UNSUBSCRIBE:
                    handleUnsubscribe(session, (UnsubscribeMessage) msg);
                    break;    
                case PUBLISH:
                    handlePublish(session, (PublishMessage) msg);
                    break;
                case PINGREQ:
                    session.write(new PingRespMessage());
                    break;
                case DISCONNECT:
                    handleDisconnect(session, (DisconnectMessage) msg);
                    break;
            }
        } catch (Exception ex) {
            LOG.error("Bad error in processing the message", ex);
        }
    }

    protected void handleConnect(IoSession session, ConnectMessage msg) {
        LOG.info("handleConnect invoked");

        m_messaging.handleProtocolMessage(session, msg);
    }

    protected void handleSubscribe(IoSession session, SubscribeMessage msg) {
        LOG.debug("handleSubscribe, registering the subscriptions");
        m_messaging.handleProtocolMessage(session, msg);
    }
    
    private void handleUnsubscribe(IoSession session, UnsubscribeMessage msg) {
        LOG.info("unregistering the subscriptions");
        m_messaging.handleProtocolMessage(session, msg);
    }

    protected void handlePublish(IoSession session, PublishMessage message) {
        m_messaging.handleProtocolMessage(session, message);
    }

    protected void handleDisconnect(IoSession session, DisconnectMessage disconnectMessage) {
        m_messaging.handleProtocolMessage(session, disconnectMessage);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        session.close(false);
    }

    public void setMessaging(IMessaging messaging) {
        m_messaging = messaging;
    }

}
