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
  -v "$(pwd)/config":/mosquitto/config \
  -v "$(pwd)/data":/mosquitto/data \
  -v "$(pwd)/log":/mosquitto/log \
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
    volumes:
      - ./config:/mosquitto/config
      - ./data:/mosquitto/data
      - ./log:/mosquitto/log
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
docker exec 
```
### Local
```bash
mosquitto_pub -h localhost -t "test/topic" -m "Hello from Fedora!" # Publish a message
mosquitto_sub -h localhost -t "test/topic" # Subscribe
```
