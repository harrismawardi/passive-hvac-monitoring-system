# Passive HVAC Monitoring System

This code base is influenced by an architecture project I worked on in 2021. For this project, I developed a hostel that utilised vegetation to filter air from particulates within a natural ventilation system. 

If you are interested more in this building project look [here](https://github.com/harrismawardi/passive-hvac-monitoring-system/blob/87e67a9e501a8a8e9f340a234d5472a86107b564/A%20Building%20of%203%20Cores%20-%20Technical%20Design.pdf).

Features:
- An API that receives sensor data (humidity, wind speed, particulates)
- Data is saved to a .json file (temp local store)
- Scheduled task to process received data saved to file
- Sensor data is compared to thresholds to determine 'feedback' actions

Under development:
- Potential to refactor to receive sensor data from event broker;
- Implement redis-cache for temp sensor detail store
