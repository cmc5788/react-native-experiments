# TODO

1. Base view/presenter classes or utils

2. ~~Do we need a way to mark a presenter/view pair as destroyed / disabled?~~ (So far no need)

3. ~~Clean up concept of view "tags", too much duplication.~~ (Cleaned up, divided into tag base and extras)

4. ~~Also decide how tags relate to logical equality of views for nav purposes.~~ (tag base + extras make views logically equal, tag base alone keys for presenters and view factories)

5. ~~Add saving view state~~ (Done)

6. ~~Clean up nav transitions to handle passing params. Passed params should be become a part of saved view state ^^^~~ (Added otherCreate event, giving any active presenter a chance to perform arbitrary actions on created presenters)

7. ~~Decide if nav "forward, backward, replace" (shamelessly stolen from Flow) are useful concepts. Can they be simplified to simply "transition anim" vs "no transition anim"?~~ (Nope, not needed.)

8. Concept of scope other than "implicit singleton" for homebrew DI stuff. Useful for view-presenter relationship.

9. Put DCL idiom into Scoped class instead of duplicating it for each Scoped

10. Splash screen utilities - something purely native that can be shown while app is warming up, JS layer is loading.

11. Pull in custom view transitions in native code

12. ~~Collapse init and afterRestore?~~ (Done - no more afterRestore, init just doesn't happen until state is fully restored and ready)

13. ~~Ways to mask state where view is created and visible but waiting to get its first commands from JS. "Flickering"~~ (Done - delay visibility of native layout until JS layer acks init.)

14. ~~Bulk navigations, setting entire new nav state instead of one modification at a time~~ (setStack added)

15. ~~Native view state save/restore independent of JS (with sparse parcelabel arrs etc)~~ (Done - native sparse parcelable arrs tracked in native code, persisted to saved state bundle across activity insts. native hierarchy restored after first round of commands sent in JS init.)

16. Support for sibling & child VPs, as in adapterview, recyclerview

17. ~~Support for localizable content access thru JS~~ (proof of concept added in JSContent)
