# react-native-experiments

Experiment with making React Native apps more Native and less React.

## Project Goals & Opinions

1. It __DOES__ makes sense to leverage the reasonably performant and likely-to-be-maintained JS layer provided by React Native to provide a single repo solution for native apps that would like to share some Controller / Presenter logic.

2. It __DOES NOT__ make sense to leverage React's cross-platform pseudo-CSS for the view layer. Native apps are very good at defining fast, modern UI - through XML, IB, [Anko](https://github.com/Kotlin/anko), [SnapKit / Masonry](https://github.com/SnapKit), or just code. Attempts to reinvent native UI, abstract it away, or otherwise subvert it are the downfall of many.

3. It would be nice to have a framework that's light on the React and heavy on the Native. It should utilize modern MVP architecture on the native side, facilitated by the JS only where necessary to bolster the architecture by sharing logic judiciously.

4. That's it for now.

## TODO

1. So far, only the Android source contains anything but the React sample project. Polish up first pass at contracts on Android, then investigate iOS implementation.

2. Obviously, need a lot of cleanup (re: naming). Everything at this stage is prototyped ad-hoc to get a working feel for the setup.
