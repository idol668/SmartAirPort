# Smart Airport

Short Instructions for running our simulation:

	1. Go to the SmartAirport.Specta file and right click on it.
	2. Choose in the Specta option the synthesize a just-in-time symbolic controller option.
	3. Go to the src dircectory and right click on SmartAirport.java
	4. Choose the option to run the java file as a java Application.
	
## Simulator

[original image](docs/jui_1.PNG)

[original image](docs/jui_2.PNG)
In order to draw the animation, the simulation draws each state one at a time.

Minimal requirement is a resolution above 1150*850.

### Injecting airplanes 
1. select takeoff/landing planes to arrive to the desired platform using toggle buttons.
2. Select type of airplane to appearusing dropdown.
3. Press Go button.
4. Wait until the desired platform willbe clear to see airplane appear.

[original image](docs/jui_3.PNG)

### Emergency landing
1. Select a landing platform from which an airplane will perform emergency landing.
2. Press Go button.
3. Policy- no other airplane transportis allowed and rescue team will arrive to the runway the airplane landed on.

[original image](docs/jui_4.PNG)
