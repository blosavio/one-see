[:section#usage
 [:h2 "Usage"]

 [:p "There are two phases to using the "
  [:code "one-see"]
  " library. First, we establish the relationships by creating a special data
 structure. Second, we get data by doing look ups in that special data
 structure."]

 [:p "Internally, our relationship data is contained in Clojure hash-maps or
 records with just a tiny bit of checking sprinkled on top. A "
  [:code "LookUp"]
  " instance provides the methods to manage that data, such as checking prior to
 pushing new data."]

 [:p "Let's recall our name+color+id flower data from the "
  [:em "Introduction"]
  "."]

 [:table
  [:tr
   [:th "name"]
   [:th "color"]
   [:th "ID"]]

  [:tr
   [:td "rose"]
   [:td "red"]
   [:td "101"]]

  [:tr
   [:td "hibiscus"]
   [:td "orange"]
   [:td "102"]]

  [:tr
   [:td "sunflower"]
   [:td "yellow"]
   [:td "103"]]]

 [:p "If we consider the top row of the table, we see the conceptual
 categories for our flower data: "
  [:em "name"]
  ", "
  [:em "color"]
  ", and "
  [:em "ID"]
  ". Focusing on just the second row of our table, we see three pieces of
 related data: "
  [:em "rose"]
  ", "
  [:em "red"]
  ", and "
  [:em "101"]
  ", corresponding to the conceptual categories. In Clojure, it's natural to
 model this relationship with a hash-map."]

 [:pre [:code "{:name \"rose\" :color :red :id 101}"]]

 [:p "We may treat the two trailing rows similarly."]

 [:pre
  [:code "{:name \"hibiscus\" :color :orange :id 102}"]
  [:br]
  [:code "{:name \"sunflower\" :color :yellow :id 103}"]]

 [:p "Let's bundle those three rows inside a single vector."]

 [:pre
  [:code "[{:name \"rose\"      :color :red    :id 101}"]
  [:br]
  [:code " {:name \"hibiscus\"  :color :orange :id 102}"]
  [:br]
  [:code " {:name \"sunflower\" :color :yellow :id 103}]"]]

 [:p "Notice how the data values of each row are unique within its column. There
 is only one "
  [:code "\"rose\""]
  " entry in the "
  [:code ":name"]
  " column, and only one "
  [:code ":orange"]
  " entry in the "
  [:code ":color"]
  " column, and only one "
  [:code "103"]
  " entry in the "
  [:code ":id"]
  " column, etc. It is this uniqueness condition that allows us to do a look up
 such as "
  [:em "What is the ID of the flower with the color yellow?"]
  " Without that uniqueness condition, we might receive multiple answers."]

 [:p "This is a good time to veer off onto a parallel track to reconsider our
 choice of hash-maps. Hash-maps are broadly useful because they are permissive.
 However, in this case, that permissiveness works against us. Records, however,
 provide a smidgen of useful constraint."]

 [:p "A record conveys the idea "
  [:em "These, and only these, are the fields of our expected data"]
  ", and mechanically enforces that idea. Let's explicitly stipulate that every
 row has exactly a "
  [:em "name"]
  ", a "
  [:em "color"]
  ", and an "
  [:em "id"]
  " by defining a record."]

 [:pre (print-form-then-eval "(defrecord Flower [name color id])")]

 [:p "Now, we create an instance that holds the row of data for a rose."]

 [:pre (replace-ns-str
        (print-form-then-eval "(->Flower \"rose\" :red 101)" 75 75)
        replacement-ns-str)]

 [:p "And just for completeness, we'll re-make our vector, exchanging hash-maps
 for records."]

 [:pre
  [:code
   "[(->Flower \"rose\" :red 101)
 (->Flower \"hibiscus\" :orange 102)
 (->Flower \"sunflower\" :yellow 103)]"]]

 [:p "We've improved our situation somewhat. We've explicitly declared our
 expected fields as exactly "
  [:code ":name"]
  ", "
  [:code ":color"]
  ", and "
  [:code ":id"]
  ". Clojure will not allow us to make a "
  [:code "Flower"]
  " instance with less than or more than those three values."]

 [:p "In addition to all rows containing exactly the same fields, a symmetric
 one-to-one lookup requires each value to be unique from the others in its
 column. But there is nothing about a series of hash-maps or records nested
 within a vector that enforces that critical condition. A "
  [:code "LookUp"]
  " instance provides that guarantee."]

 [:p "We create a "
  [:code "LookUp"]
  " instance with the "
  [:code "look-up"]
  " function. The preferred 2-arity version accepts a sequence of row datums..."]

 [:pre [:code
        "[[\"rose\" :red 101]
 [\"hibiscus\" :orange 102]
 [\"sunflower\" :yellow] 103]"]]

 [:p "...and a record constructor, in this case, "
  [:code "->Flower"]
  "."]

 [:pre
  (replace-ns-str
   (print-form-then-eval "(look-up [[\"rose\" :red 101]
                                    [\"hibiscus\" :orange 102]
                                    [\"sunflower\" :yellow 103]]
                                   ->Flower)" 50 75)
   replacement-ns-str)]

 [:p "Yikes. That's gnarly. Let's inspect the internal representation by
 invoking the "
  [:code "table"]
  " method."]

 [:pre
  (replace-ns-str
   (print-form-then-eval "(table (look-up [[\"rose\" :red 101]
                                           [\"hibiscus\" :orange 102]
                                           [\"sunflower\" :yellow 103]]
                                          ->Flower))" 55 75)
   replacement-ns-str)]

 [:p "That looks okay. We can see all our flower data arranged as we expect.
 What's not immediately apparent is that before pushing each record onto the
 table, "
  [:code "look-up"]
  " checked to see if "
  [:ol
   [:li "All fields match the others."]
   [:li "The values are unique within a column."]]

  "In this case, Condition 1 is provided by virtue of using the "
  [:code "->Flower"]
  " constructor, but such checking would be necessary if we chose to supply "
  [:code "look-up"]
  " with regular hash-maps. Regardless of whether we chose records or hash-maps,
 condition 2 can only be ensured by our "
  [:code "LookUp"]
  " instance's internal checking."]

 [:p "Let's try to make an instance where the third row's flower is also red,
 illegally repeating the rose's color."]

 [:pre
  (replace-ns-str
   (print-form-then-eval "(try (look-up [[\"rose\" :red 101]
                                         [\"hibiscus\" :orange 102]
                                         [\"tulip\" :red 103]]
                                        ->Flower)
(catch Exception e (.getMessage e)))" 55 75)
   replacement-ns-str)]

 [:p "Nope. "
  [:code "look-up"]
  " won't let us have both red roses and red tulips. That's exactly the
 guarantee we want, because when we ask "
  [:em "What is the flower that is red?"]
  ", we want only one answer. In fact, let's ask that question. To streamline
 the discussion, we'll create a "
  [:code "LookUp"]
  " instance containing flower data and bind it to a symbol."]

 [:pre
  (print-form-then-eval "(def flowers-3 (look-up [[\"rose\" :red 101]
                                                  [\"hibiscus\" :orange 102]
                                                  [\"sunflower\" :yellow 103]]
                                                 ->Flower))")]

 [:p "Then, using the "
  [:code "get-val"]
  " method, we retrieve the value."]

 [:pre
  (print-form-then-eval "(get-val flowers-3 :color :red :name)")]

 [:p "From left to right, it reads "
  [:em "From the "
   [:code "flowers-3"]
   " table, find the row whose "
   [:code ":color"]
   " is "
   [:code ":red"]
   ", and return the value associated to "
   [:code ":name"]
   "."]]

 [:p "One of the Clojure's niceties is that collections implement the function
 interface, so that they do something useful when invoked with the appropriate
 argument. "
  [:code "LookUp"]
  " instances behave similarly. We simply drop the "
  [:code "get-val"]
  "."]

 [:pre (print-form-then-eval "(flowers-3 :color :red :name)")]

 [:p "Let's do a quick demonstration of name+color and color+name that the
 plain hash-map struggled with earlier. "]

 [:table
  [:tr
   [:th "name"]
   [:th "color"]]

  [:tr
   [:td "rose"]
   [:td "red"]]

  [:tr
   [:td "hibiscus"]
   [:td "orange"]]

  [:tr
   [:td "sunflower"]
   [:td "yellow"]]]
 
 [:p "First, we create a symmetric one-to-one relationship."]

 [:pre (print-form-then-eval "(def flowers-4 (look-up [{:name \"rose\" :color :red}
                                                       {:name \"hibiscus\" :color :orange}
                                                       {:name \"sunflower\" :color :yellow}]))")]

 [:p "The "
  [:code "LookUp"]
  " instance returned by "
  [:code "look-up"]
  " enforces our uniqueness requirements. Only rose is red, only hibiscus is
 orange, and only sunflower is yellow."]

 [:pre
  (print-form-then-eval "(flowers-4 :name \"rose\" :color)")
  [:br]
  (print-form-then-eval "(flowers-4 :name \"hibiscus\" :color)")
  [:br]
  (print-form-then-eval "(flowers-4 :name \"sunflower\" :color)")]

 [:p "Furthermore, red is only rose, orange is only hibiscus, and yellow is only
 sunflower."]

 [:pre
  (print-form-then-eval "(flowers-4 :color :red :name)")
  [:br]
  (print-form-then-eval "(flowers-4 :color :orange :name)")
  [:br]
  (print-form-then-eval "(flowers-4 :color :yellow :name)")]

 [:p "Finally, let's really stretch by adding a couple more columns: "
  [:em "family"]
  " and "
  [:em "leaves"]
  "."]

 [:table
  [:tr
   [:th "name"]
   [:th "color"]
   [:th "ID"]
   [:th "family"]
   [:th "leaves"]]

  [:tr
   [:td "rose"]
   [:td "red"]
   [:td "101"]
   [:td "Rosaceae"]
   [:td "pinnate"]]

  [:tr
   [:td "hibiscus"]
   [:td "orange"]
   [:td "102"]
   [:td "Malvaveae"]
   [:td "lanceolate"]]

  [:tr
   [:td "sunflower"]
   [:td "yellow"]
   [:td "103"]
   [:td "Asteraceae"]
   [:td "cardioid"]]]

 [:p "First, we define a new record with those two additional fields, "
  [:code "family"]
  " and "
  [:code "leaves"]
  "."]

 [:pre (print-form-then-eval "(defrecord Flower-power [name color id family leaves])")]

 [:p "Next, we create a new "
  [:code "LookUp"]
  " instance with "
  [:code "look-up"]
  " and name it "
  [:code "flowers-5"]
  "."]

 [:pre (print-form-then-eval "(def flowers-5 (look-up [[\"rose\" :red 101 \"Rosaceae\" :pinnate]
                                                       [\"hibiscus\" :orange 102 \"Malvaveae\" :lanceolate]
                                                       [\"sunflower\" :yellow 103 \"Asteraceae\" :cardioid]]
                                                      ->Flower-power))")]

 [:p "Now, let's try some look ups."]

 [:pre
  (print-form-then-eval "(flowers-5 :name \"rose\" :family)")
  [:br]
  (print-form-then-eval "(flowers-5 :id 103 :leaves)")
  [:br]
  (print-form-then-eval "(flowers-5 :family \"Asteraceae\" :name)")]

 [:p "Dandy."]

 [:h3 "Performance considerations"]

 [:p "A "
  [:a {:href "#naive"} "naive"]
  " implementation might be "
  [:em "O(n)"]
  " in time, walking the sequence of rows, and requiring a compare at each
 step."]

 [:p "A "
  [:code "LookUp"]
  " instance leans on Clojure's structural sharing to make cheap copies of the
 data. With those cheap copies appropriately arranged, getting a value is
 merely "
  [:em "O(1)"]
  " in time, with zero compares. In fact, any value may be retrieved with
 exactly three hash-map look-ups."]

 [:p "Though efficient, the "
  [:code "One-see"]
  " library is intended for a few dozen rows and a handful of columns, populated
 by hand, and not exposed to the world. If you feel tempted to put it on a hot
 path it'll probably work okay, but consider some "
  [:a {:href "#alternatives"} "alternatives"]
  ", or dust off your favorite database."]]

