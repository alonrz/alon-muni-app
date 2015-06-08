#This is the journal page for Alon's Muni app where updates on progress made is being displayed.

# Introduction #

I will be adding journal entries here weekly and sometimes daily about:
  * The progress I've made (once you start coding, mention the revisions for that week).
  * The problems I have encountered.
  * Lessons learned.
  * What is planned for the following week.




# Entries #
## May 22nd ##
Progress made:

I've prepared and presented a presentation about the application.
Added to show predictions when selecting a single line and station in fragment form.
Added menu items to match the fragments for refresh and display inbound/outbound.
Manu bar was added back to the top of the tabs by manipulating the style and theme used.
Splash screen added to show loading messages.
DB was built and infrastructure added to support it but feature was taken down due to time restrictions.
Added inbound and outbound indicator when appropriate.
Made the code a little more organized with eliminating double calls to SFMTA and double processing of the data. Some threads were taken completely off and their tasks were moved under the splash screen.

Problems I've encountered:
The transition from activities to fragments was not as smooth as I hoped it would be. The tabs were ruined in the process and took a really long time before I was able to fix it.
Something also changed in the list order and its not as accurate now, the M line keeps disappearing and reappearing with no logic I could find to reproduce. Since I am working with live data, this is hard to solve.
Compass seems like its not always accurate but since I am always working near a computer, that may have some effect on the magnetic field.

Lessons learned:
Learned to change themes and work with fragments and all their sub-issues (menus, tabs, titles, etc)

Future Plans:
  * fix the list of predictions and make sure it is accurate.
  * add map
  * add component to the compass to show inbound and outbound separately and not as 1 image.


## May 10th ##
Progress made:

In an attempt to get rid of the main debug activity which leads to all else, I told the manifest that the new activity to start with is the tab controller. This made a mess of the tabs as well as the menus. Tried to fix it. Finally it was a matter of using  a different theme which returned the menu and tabs to work.
Also started to work on favorites. Learning to use sqlite and adding star buttons to each prediction to be saved.



## April 18th ##
Progress made:

This week I am trying to implement tabs with swipe action as the main UI for my application, and also the compass feature to tell inbound from outbound in a specific location for a line.
I will need to switch my application to use Fragments instead of Activities and will have to learn how to use those objects.
I will also learn how to use the compass functionality as well as decide how to implement this in the context of lines.
I have added a mechanism to control the GPS listeners and disconnect it when the app is in the background to conserve battery but be consistant throughout the application.

Problems I've encountered:

Fragments require a new way of interacting with my code and layouts. Need to work with new methods and a new design. I will attempt to use the ViewPagerIndicator object for this. For the compass I will see how to decide on directions when multiple lines are present. I may ask the user to select 1 line for more accurate results.

Lessons learned:

Learned to use the new compass orientation action and Fragments.

Future Plans:
  * Prepare presentation for class next week
  * Show predictions when selecting a single line and station.
  * add menu tab on top (in bar)
  * Add splash screen for initial loading
  * Build DB for stations locations.
  * Add inbound/outbound indicator
  * maps of stations only (maybe routes too)


## April 9st ##
Progress made:

This week was all about getting real live results and working on improving the UI with better display.
First I found all stations near me, within a certain radius and removing double stations or multiple stations for the same line if they are in the same radius.
Predictions are now displaying stations near you and have both inbound and outbound lists.
UI was improved to show line numbers with their respective colors (circle for train and square for bus) in a nice format with rounded corners for each predictions. If no prediction, it will not be shown.
Of course build more functions to read the new XML of predictions.
Added icon.
Added refresh option for predictions.

Problems I've encountered:

UI took a lot to perfect but it looks good now. Still have positioning problems when the text of the route name is too large (like M-OWL) and I try to make the font smaller - it moved the position of the entire circle.

Lessons learned:

Learned a lot about UI and using shapes with a different XML file to use as background.
Learned how to make an icon using GIMP.

Planned next week:
  * Show predictions when selecting a single line and station.
  * add menu tab on top (in bar)
  * Add splash screen for initial loading
  * Build DB for stations locations.
  * Add inbound/outbound indicator
  * maps of stations only (maybe routes too)
  * fix GPS when leaving the app.


## April 1st ##
Progress made:

GPS coords are displayed on the main screen.
I have switched to SAX for XML reading as Android documentation recommends it for faster handling of large files.
Built a DataManager to handle XML fetching and parsing. Is static and singleton so it can be access in all activities.
Build SAX mechanism with XmlPullParser. Handles a general file with list of all routes, their stations and directions (including order of station per direction)
Learned how to use a custom adapter for lists with various views and headers.
Added menu items for inbound and outbound toggle.

Problems I've encountered:

File still takes 3-4 seconds to be uploaded and parsed. Need to find a way to do in background but user can still press buttons and move to other screens. Need to find a way to respond to thread finishing by halting activity or showing a progress bar in different activities.
Did not find how to update map with live data that keeps changing. Left it for now and will show static maps.


Lessons learned:

XML file from SFMTA can be shrinked with a special parameter. instead of 1.4M it is now 700K. Still large - think of caching some info into SQLite.
Learned to use the Adapter for listview. How to use it to set up custom views inside a list. This will enable subtext and titles and also images.
Learned how to work with menus and change them dynamically to respond to state on screen (toggle inbound and outbound)

Planned next week:
  * show predictions for that station in textual format only
  * add menu tab on top (in bar)
  * Find stations near me.
  * Add to those stations predictions of coming vehicles.
  * find a way to add progress bar and hide the loading of the initial data.
  * Incorporate icons to lists.

## Match 21st (midweek update) ##
Progress made:
  * maps are now working, involved many inserts to AndroidManifest, Inserting a check for GooglePlayServices being the latest, and debugging for errors due to crash. Right now it does show the map but of no specific location.
  * main screen was changed to linearlayout. Still contains only test buttons.


## Match 16th ##
Progress made:
  * Construct a list in UI. That list was made of textviews in a simple linear format. bar by default on top with no functionality yet. Button on main screen starts the process of fetching data.
  * Collected a list of routes from SFMTA XML
  * Collected a list of stops per route from SFMTA XML (respond to click on a route)
  * Display both lists, after picking a route it will show its stations.
  * Classes constructed: Route, Stop

Problems I've encountered:
  * Nothing serious so far, just learning the environment.
  * Had to encode the URL and use XML DOM for parsing.
  * Need to think of how objects should be passed between Activities and which.

Lessons Learned:
  * How to change title of bar at top.
  * Getting XML from URL via thread and update list when its done
  * How to read the XML given from SFMTA.

Planned next week (and spring break):
  * Get GPS coordinates to display on main screen.
  * Add inbound/outbound selection after picking a station
  * show predictions for that station in textual format only
  * learn to work with maps new version.
  * rethink app UI to match Jellybean new UI standards (less menus, more tabs).
  * add menu tab on top (in bar)
  * Thing of class design for data flow between activities.