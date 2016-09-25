'''
Created on 17 de ago. de 2016

@author: Maca
'''

import json
import threading
import time
import random
import sys
from kafka import KafkaProducer
from kafka.errors import KafkaError
from datetime import datetime

numParameters  = len(sys.argv)
if (numParameters != 4): 
	print ("Program arguments <kafka broker> <taxiStands json file> <time simulation>")
	sys.exit()

bootstrap_servers_conf = sys.argv[1]
json_path = sys.argv[2]
aimeStandsSimulation = int(sys.argv[3])

producer = KafkaProducer(bootstrap_servers=bootstrap_servers_conf, value_serializer=lambda m: json.dumps(m).encode('ascii'))


def import_json_file(path):
    file_data = open(path)
    json_data = json.load(file_data)
    return json_data
    
def format_dictionary(json_data):
    formatted_stands = {}
    each_stand = {}
    taxi_stands = json_data['stands']    
    for stand in taxi_stands:
        each_stand = {}
        id = stand['id']
        each_stand['latitude'] = stand['latitude']
        each_stand['longitude'] = stand['longitude']
        each_stand['numPlazas'] = stand['numPlazas']
        each_stand['distrito'] = stand['distrito']
        each_stand['direccion'] = stand['direccion']
        formatted_stands[id] = each_stand
        
    return formatted_stands


def worker(id, total_stands):
    """funcion que realiza el trabajo en el thread"""
    random_busystands = random.randint(1, total_stands)
    busy_stands = random_busystands 
    while 1:        
        option = random.randint(0, 2)
	time.sleep(random.randint(aimeStandsSimulation/4,aimeStandsSimulation*2.5))
        
        if (1 == option) and (0<busy_stands):
            busy_stands = busy_stands -1
            dateTime = datetime.utcnow().strftime('%Y-%m-%d %H:%M:%S.%f')[:-3]
            #Send arrival message to Kafka
            print (str.format("Taxi leaving in stand {0}",id))
            producer.send('taxi-in-stand', {'id': id, 'dateTime': dateTime, 'inOut':False})
        elif (1 == option) and (0 == busy_stands):
            #Possible kafka message reporting busy places
            print(str.format("All places in taxi Stand: {0} are free",id))
        elif (0 == option) and (total_stands>busy_stands):
            busy_stands = busy_stands +1
            dateTime = datetime.utcnow().strftime('%Y-%m-%d %H:%M:%S.%f')[:-3]
            #Send departure message to Kafka 
            print (str.format("Taxi coming stand {0}",id))
            producer.send('taxi-out-stand', {'id': id, 'dateTime': dateTime, 'inOut':True})
        
    return
    
'''   
def get_available_stands():
    return (self.total - self.busy)
'''
def create_stands_and_threads(formatted_stands):
    stands_ids = formatted_stands.keys()
    for id in stands_ids:
        stand_data = formatted_stands[id]
        places = stand_data['numPlazas']        
        new_taxi = threading.Thread(target=worker, args=(id,places,))
        new_taxi.start()


if __name__ == '__main__':
    
    json_data = import_json_file(json_path)
    formatted_stands = format_dictionary(json_data)
    create_stands_and_threads(formatted_stands)
    pass
