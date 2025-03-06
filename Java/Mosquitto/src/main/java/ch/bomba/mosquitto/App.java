package ch.bomba.mosquitto;

import java.util.UUID;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

/**
 * Hello world!
 */
public class App
{
  public static void main(String[] args)
  {
    String TOPIC = args[0];
    String TYPE = args[1];
    String ADDRESS = args[2];
    //System.out.println("bomba.cat moment!");
    
    try 
    {
      MqttClient client = new MqttClient(ADDRESS, UUID.randomUUID().toString());
      
      if (TYPE.equals("SUB"))
      {
        client.subscribe("KILL", new IMqttMessageListener()
        {
          @Override
          public void messageArrived(String topic, MqttMessage message) throws Exception
          {
            System.out.println("Receoved Topic: " + topic + ", Message: " + message);
          }
        });
      } else if (TYPE.equals("PUB"))
      {
        double increment = 0;
        while (true)
        {
          client.connect();
          Double value = Math.sin(increment);
          MqttMessage message = new MqttMessage(String.valueOf(value).getBytes());
          increment = (increment+0.1) % (2*Math.PI);
          client.publish(TOPIC, message);
          client.disconnect();
          Thread.sleep(1000);
        }
      } else
      {
        System.out.println("Missing Argument: TYPE, please set this to SUB or PUB");
      }
    } catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
