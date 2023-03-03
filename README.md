schoolio shows you a huge list of public schools in nyc, and lets you pick one from the list to
see its SAT test data if available. it's very plain but my goal was to choose function
over form on a short timeframe. given more time i would have made some different choices:
- add some unit tests. testing to make sure the livedata is being updated with api responses, etc
- display more data / give the user more options. allow users to send an email or call the selected school
- make it look better. apply a material theme, pick some good colors etc
- add some filtering options: allow users to pick schools based on location, average sat scores etc

design choices: 
- normally i'd work in 100% kotlin, but the requirements asked for java / a combination. i tried to
    keep kotlin limited to places where i could take advantage of coroutines for async calls and long
    running tasks, as well as things like runCatching for error handling. the response model classes
    are also huge because they contain getters / setters for everything included in the response.
    these can be deleted in the java class but i left them in the interest of expandability.
- one activity with multiple fragments: it's a very small app with only two screens, so i made the 
    choice to avoid overcomplication / over use of resources. generally i'd use the jetpack navigation
    library in addition to be able to pass school /sat response objects around
- architecture: again made in the interest of expandability / separation of concerns. the ui doesn't
    need to know about the network calls, etc. 
- library choices: these are pretty standard- hilt for dependency injection to avoid doing it manually,
    retrofit for making the network calls and gson for parsing the responses to objects.