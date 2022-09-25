# Catlr

Lots of cats

### Approach to the project

I started by reading the requirements carefully and doing some brief research on which libraries
would be most appropriate to use for solving the given use case:

- I went with Ktor for networking as I had used it before and it plays nicely with quite coroutines
  and kotlin's serialization library.
- For image loading there was a lot of choice: I went with Fresco as it handles animated GIFs well
  and there is a nice and easy Jetpack Compose integration out there.
- I chose the latest pagination library with Jetpack Compose integration. I had not used this
  library before but was able to find it straightforward enough to get up and running.
- For dependency injection I went with Koin which is lightweight and suited my simple needs but
  still scales up well for larger projects.

After building out the general classes I knew I'd need for the MVVM architecture, I got to work
playing around with the API and getting the data request and parsing layer working. After that, I
setup and fleshed out the logic for paging using the API and getting the data dumped on the screen.
The majority of the rest of the time was spent on constructing the UI including a settings dialog
that allows the user to choose between still images, animated gifs, or both, as well as adding
additional filters. As I approached the 4 hour mark I wrapped up by tidying up some of the colours
and styling and adding a basic test for the filtering logic.

Given more time I would like to have written some more tests particularly with error cases around
networking and loading failures as well as giving the UI a bit more polish.