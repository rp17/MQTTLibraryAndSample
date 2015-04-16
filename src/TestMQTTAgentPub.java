

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.sample.MQTTAgent;



public class TestMQTTAgentPub implements MqttCallback {
	
	public static void main(String[] args) throws Exception {
		//String nodeID = getNodeMacAddress();
		String nodeID = "agentPub";
		String brokerIPAddress =  "127.0.0.1"; //broken in this case is running in the same computer, it can be running in a different machine 
		int brokerPort = 1883; //default port for Really Small Message Broker - RSMB https://www.ibm.com/developerworks/community/groups/service/html/communityview?communityUuid=d5bedadd-e46f-4c97-af89-22d65ffee070
		String topic = "shared"; //each MQTT agent can subscribe to multiple topics.  MacAddress + "In" is a default topic for each agent
		//String brokerUrl, String clientId, boolean cleanSession, boolean quietMode, String userName, String password
		String protocol = "tcp://";
		String url = protocol + brokerIPAddress + ":" + brokerPort;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		
		String publishMessage = "Node: " + nodeID + " Time: " + dateFormat.format(cal.getTime());
		
		MQTTAgent mqAgent = new MQTTAgent(url,nodeID,false, true, null, null );
		mqAgent.setCallBack(new TestMQTTAgentPub());
		//QoS (0-means FireAndForget which is fastest; 1- means StoreAndForwardWithDuplicate which is bit slow; 2- means StoreAndForwardWithoutDuplciate which is slowest
		mqAgent.publish(topic, 1, publishMessage.getBytes());
		mqAgent.subscribe(topic, 1);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));	
		try {
			br.readLine();
		} 
		catch (IOException ioe) {}
//		
//		MessageQulityOfService[] subscriptionTopicQoS = { MessageQulityOfService.StoreAndForwardWithPossibleDuplicates }; //for each subscribed topic, there is an assigned QoS level, which is intuitive.
//		String publishTopic = nodeID + "Out"; //each MQTT agent can publish to any topics.  Default value is Mac
//		MessageQulityOfService publishTopicQoS = MessageQulityOfService.StoreAndForwardWithPossibleDuplicates;
//		
//		MQTTManager mqAgent = new MQTTManager( nodeID, brokerIPAddress, brokerPort);
//		//as a library, the MQTT manager has to be both subscriber and publisher.  Well it is a non-intuitive requirement
		//but given the task for both the evaluation application and the runtime verification middleware, to combine both together
		//is quickest and turns out most efficient in saving threads/avoiding concurrent common pitfalls -> especially when 
		//mqtt is required for Android.
//		mqAgent.setSubscribeTopics(subscriptionTopic, subscriptionTopicQoS);
//		mqAgent.connectToBroker(); //synchronous call, only when connected, when proceed to the next step.  Asynchronous connection is built, but not exposed yet
//		mqAgent.publishResponse(publishTopic, "Hello World".getBytes(), publishTopicQoS);
	}
		
	private static String getNodeMacAddress(){
		 try {
			    InetAddress ip = InetAddress.getLocalHost();
			    System.out.println("Current IP address : " + ip.getHostAddress());

			    Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
			    while(networks.hasMoreElements()) {
			      NetworkInterface network = networks.nextElement();
			      byte[] mac = network.getHardwareAddress();

			      if(mac != null) {
			        System.out.print("Current MAC address : ");

			        StringBuilder sb = new StringBuilder();
			        for (int i = 0; i < mac.length; i++) {
			          sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			        }
			        System.out.println(sb.toString());
			        return sb.toString();
			      }
			    }
			    return null;
		  } catch (UnknownHostException e) {
		    e.printStackTrace();
		    return null;
		  } catch (SocketException e){
		    e.printStackTrace();
		    return null;
		  }
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Topic: " + arg0);
		System.out.println("Message: " + new String(arg1.getPayload(), "UTF-8"));
	}

}
