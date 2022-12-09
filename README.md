# Repository for semestral project from Mobile Computing subject
In the project are used navigation, fragments, databinding, roomdatabase, recyclerviews, viewmodels, coroutines, retrofit, overpass api, location services and shared preferences
# Project screens:
- Registration 
- Login 
- Pub/restaurant/bar list
- Pub/Bar/Restaurant detail 
- Nearby pub/restaurant/bar screen within a radius of 500 meters
- Friends 
- Add friends
- Remove friends
## Registration screen 
- contains inputs for username, password, repeat password and buttons for submit registration and login
- after clicking on submit registration button, user info are trying to be registered and if successful user is redirected to pub/restaurant/bar screen
- after clicking on login button, user is redirected to Login screen
## Login screen 
- contains inputs for username, password, buttons for submit login and registration
- after clicking on submit login button, user information are authenticated and if submited information are right then he/she is redirected to    Pub/Restaurant/Bar list
- after clicking on registration button, user is redirected to Registration screen
## Pub/Restaurant/Bar screen 
- contains list of pubs/restaurants/bars, where at least one user is checked in, buttons for redirect to nearby pub/restaurant/bar screen and friend list
- each row contains pub/restaurant/bar name, number of people checked in and the distance from user location to the pub/bar/restaurant
- user is able to refresh the screen, sort datas by name/users checked in/distance
## Nearby Pub/Bar/Restaurant screen 
- contains list of pubs/bars/restaurants in a radius of 500 meters from user location and posibility to check into any pub/restaurant/bar
- user need to have GPS turned on to find the neariest pub/restaurant/bar
## Friend list screen 
- contains list of friends, who have added user to their friends
- in the list user is able to see his/her friends username and current pub/restaurant/bar
## Add/remove friends screen
- contains input for username of a friend and button for adding/deleting the friend

