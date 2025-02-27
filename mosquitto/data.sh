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
