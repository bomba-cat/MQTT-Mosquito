# MQTT

## Whats MQTT?
MQTT is a lightweight messaging protocol designed for devices with limited power or bandwidth. It's perfect for IoT devices just like sensors, cameras or embedded systems.

## Installation guide
### Docker
```bash
mkdir mosquitto
cd mosquitto
mkdir config data log
```
Next we'll create `mosquitto.conf` inside the `config/` folder.
```bash
touch config/mosquitto.conf
vim config/mosquitto.conf
```
and we'll put these lines in there
```conf
persistence true
persistence_location /mosquitto/data/
log_dest file /mosquitto/log/mosquitto.log
listener 1883
allow_anonymous true
```
Next we'll run a container now
```bash
docker run -d \
  --name mosquitto \
  -p 1883:1883 \
  -p 9001:9001 \
  -v "$(pwd)/config":/mosquitto/config \
  -v "$(pwd)/data":/mosquitto/data \
  -v "$(pwd)/log":/mosquitto/log \
  -v "$(pwd)/data.sh":/bin/data \
  eclipse-mosquitto
```
Additionally we can also add password protection by adding this to our config file
```conf
allow_anonymous false
password_file /mosquitto/config/passwordfile
```
also do this to the already existing container
```bash
docker run --rm eclipse-mosquitto mosquitto_passwd -c /mosquitto/config/passwordfile username
```
and then restart it
```bash
docker restart mosquitto
```
---
If you wish to use it with docker compose it would look this way
```yml
version: '3.8'

services:
  mosquitto:
    image: eclipse-mosquitto:latest
    container_name: mosquitto
    ports:
      - "1883:1883"  # MQTT Port
      - "9001:9001"  # WebSocket Port
    volumes:
      - ./config:/mosquitto/config
      - ./data:/mosquitto/data
      - ./log:/mosquitto/log
      - ./data.sh:/bin/data
    restart: unless-stopped
```
### Local
Note, this tutorial is for Fedora/RedHat related operating systems
---
First we install the required packages
```bash
sudo dnf install mosquitto mosquitto-clients
```
Next we enable the service for mosquitto
```bash
sudo systemctl enable --now mosquitto
```
if you wanna check if its running or not use this command
```bash
sudo systemctl status mosquitto
```
with the same config as before write it to `/etc/mosquitto/mosquitto.conf`
```bash
suduo vim /etc/mosquitto/mosquitto.conf
```
also open the firewall ports
```bash
sudo firewall-cmd --add-port=1883/tcp --permanent
sudo firewall-cmd --reload
```
## Testing
### Docker
```bash
docker ps # Check for the running mosquitto instance
docker exec -it <container_name_or_id> bash
mosquitto_pub -h localhost -t "test/topic" -m "Hello from Docker!" # Publish a message
mosquitto_sub -h localhost -t "test/topic" # Subscribe
```
### Local
```bash
mosquitto_pub -h localhost -t "test/topic" -m "Hello from Fedora!" # Publish a message
mosquitto_sub -h localhost -t "test/topic" # Subscribe
```
### Images
![img](img/DockerPub.png)
![img](img/HelloDocker.png)
### Multi topic
You can create a `data.sh` and put the following inside
```bash
if [ -z "$1" ];
then
  echo "Usage: $0 sensor_name"
  exit 1
fi

SENSOR_NAME=$1
TOPIC="sensors/$SENSOR_NAME"

while true;
do
  VALUE=$(( RANDOM % 100 )) 
  mosquitto_pub -h localhost -t "$TOPIC" -m "$VALUE"
  sleep 1
done
```
also dont forget to bind the local `data.sh` to the `/bin/data` if you are using docker for this: `-v ./data.sh:/bin/data`.
Lastly you can read the data like this
```bash
mosquitto_sub -h localhost -t "sensors/#"
```
## Visualize data with Grafana
### Setup
Install grafana oss using these commands
```bash
sudo yum install https://dl.grafana.com/enterprise/release/grafana-enterprise-11.5.2-1.x86_64.rpm
sudo /bin/systemctl daemon-reload
sudo /bin/systemctl enable grafana-server.service
sudo /bin/systemctl start grafana-server.service
```
Next up open grafana over the `localhost:3000` address in your browser
![img](img/Grafana.png)
The login credentials are
- admin
- admin
Next up go to Administration and click on Plugins and Data and then again Data
Search for the MQTT plugin and install it
Under connections and Data Sources and in there add these options:
![img](img/MQTTSettings.png)
Next up we setup a Dashboard
![img](img/Visual.png)
Add a Visualization and then add a topic, you can use # to add all topics and set the time at the top to the last 5 minutes
![img](img/Dashboard.png)

## How does the System work?
First we setup a publisher which just delivers data to a broker which subscribed to the publisher and is reading its data. We have also setup Grafana which just subscribes to the publishers and reads its data to visualize it.
