# TODO

1. Base view/presenter classes or utils

2. Do we need a way to mark a presenter/view pair as destroyed / disabled?

3. Clean up concept of view "tags", too much duplication.

4. Also decide how tags relate to logical equality of views for nav purposes.

5. Add saving view state

6. Clean up nav transitions to handle passing params. Passed params should be become a part of saved view state ^^^

7. Decide if nav "forward, backward, replace" (shamelessly stolen from Flow) are useful concepts. Can they be simplified to simply "transition anim" vs "no transition anim"?

8. Concept of scope other than "implicit singleton" for homebrew DI stuff. Useful for view-presenter relationship.

9. Put DCL idiom into Scoped class instead of duplicating it for each Scoped
