# Muni App #

## Design documents for Alon's Muni App ##
Here is where scenarios, mockups, functionality and use cases will be displayed for this application.

This application will allow users to get real time information from SF Muni system which is the same as that which is displayed on electronic boards in stations.

User will be able to look up specific lines, stations and use GPS to see relavent information by proximity.

## Functionality ##
  1. User can see all stations near him and see which lines are coming next with their times, sorted by earliest.
  1. User can see a list of all lines and select a single line from there to see all stations related to that line, in order of stations in route (may add switch to view by alphabetical order).
  1. User can mark lines and stations as favorites and see a page of only those.
  1. (optional) user who is in a station and is not sure which side is inbound and which is outbound can use the campus to point the phone in a direction of the tracks and the app will tell him what direction its going. This is to replace the inbound/outbound signs in stations which are not always visible.
  1. (optional) Add comments about cars. You can say that a car is broken, dirty, no a/c, etc...
  1. (optional) Add a car that is non existing on the map. Some phantom cars are not registered on the SF Muni API but still run. If enough users comment on one it will be added.
  1. (optional) predict how long it will take a car to run according to information from cars ahead of it. main screen


## Use Cases ##
  1. Tony needs to get from SFSU to downtown San Francisco. He wants to know when he should be at the station for the M train. He pulls out his Android phone and opens alon-muni-app (temp name). He sees on the main screen all the lines which are close to his station ordered by which is coming first. If he does not see his train in the first few options he clicks the menu and moves to "Favorites". There he sees the M train which is going downtown from SFSU station listed which he saved before. The view looks the same and he can see the next few trains coming to the station.
  1. Carla is on the F line and she just arrived at Castro station on her way to Powel stattion. She wants to know how many stations she has left before she needs to get off. She pulls out her app and clicks on the Search Line from the menu and searched for the F line for more details. A list of all stations is presented to her and the next station will be in bold or otherwise marked.


## Mockups ##
### Main Screen ###
**Show lines by proximity to user and their earliest arrivals**
![https://alon-muni-app.googlecode.com/files/Main%20Activity.jpg](https://alon-muni-app.googlecode.com/files/Main%20Activity.jpg)


### Menus options ###
**Show Menu options when clicking the menu button.**
![https://alon-muni-app.googlecode.com/files/Menus.jpg](https://alon-muni-app.googlecode.com/files/Menus.jpg)


### Specific Line Selected ###
**Show screen after the user selected a specific line to get more info about**
![https://alon-muni-app.googlecode.com/files/Specific%20Line%20Activity.jpg](https://alon-muni-app.googlecode.com/files/Specific%20Line%20Activity.jpg)