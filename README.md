# Test task solution and some features. 

## Architecture

For this task MVVM was selected, and there are different reasons for it: 

1. We need proper state management for screen rotations and MVVM suits for it, especially with...
2. Google Architecture Components release. 

Beacuse Google released Architecture Components at the moment i was given this task, i decided to start from scratch and implement this app with Google's architecture vision. 

## Some thoughts on Google Architecture Components

1. LiveData is a silly replacement of RxJava's Observables. Transformations should be provided via another class, and they are limited. 
2. ViewModel - amazing! Good component for managing Disposables and keeping state of the app.
3. Room - like it. Implemented a small DAO in Kotlin and was happy. Haven't liked Realm because of multi-threading issues, Room doesn't have that problem. 

## Some insights on the solutions. 

I started to write everything in Kotlin with LiveData, then moved to RxJava2. (branches "master" and "rxjava2")
Then i decided to use a bit of Java (because task explicitly required it), so moved development to "develop" branch. 
Dependency injection and Room classes are still maintained in Kotlin, but these are not crucial for the task. 

## Known issues 

1. Not a full git-flow standard. Keeping all latest code in develop (that's good), developing features in other branches, rebasing (that's good, too). Why do i have mess in master? My fault :(
2. ...


### Thank you a lot for the task and your time :)
