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
    String ADDRESS = args[0];
    String SUB = args[1];
    
    try 
    {
      MqttClient client = new MqttClient(ADDRESS, UUID.randomUUID().toString());
      
      client.connect();
      client.subscribe(SUB, new IMqttMessageListener()
      {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception
        {
          System.out.println("Received Topic: " + topic + ", Message: " + message);
        }
      });
      double[] increments = new double[args.length - 2];
      for (int i = 2; i < args.length; i++)
      {
        increments[i-2] = i * Math.PI / 4;
      }
      while (true)
      {
        for (int i = 2; i < args.length; i++)
        {
          int idx = i - 2;
          double value = Math.sin(increments[idx]);
          MqttMessage message = new MqttMessage(String.valueOf(value).getBytes());
          increments[idx] = (increments[idx] + 0.1) % (2 * Math.PI);
          client.publish(args[i], message);
        }
        Thread.sleep(1000);
      }
    } catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
