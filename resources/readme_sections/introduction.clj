[:section#introduction
 [:h2 "Introduction"]

 [:p "Pretend we've got some flower data with the requirement that each flower
 name has one color and each color associates to exactly one flower name."]

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

 [:p "If we'd like to know "
  [:em "What color is a rose?"]
  ", we might establish the name+color relationship with a hash-map."]

 [:pre
  (update
   (print-form-then-eval "(def flowers-1 {\"rose\" :red
                                        \"hibiscus\" :orange
                                        \"sunflower\" :yellow})" 15 15)
   1
   #(str/replace % #"(red,|orange,)" "$1\n               "))]

 [:p "Then, we could look up the value associated to \"rose\"."]

 [:pre (print-form-then-eval "(flowers-1 \"rose\")")]

 [:p " If we'd like to go in the other direction and ask "
  [:em "Which flower is orange?"]
  ", we could invert the hash-map to establish a color+name relationship."]

 [:pre
  (print-form-then-eval "(require '[clojure.set :refer [map-invert]])")
  [:br]
  [:br]
  (print-form-then-eval "(def flowers-1-inverted (map-invert flowers-1))")]

 [:p "Then, look up is analogous."]

 [:pre (print-form-then-eval "(flowers-1-inverted :orange)")]

 [:p "Manual inversion is perhaps a bit awkward, but let's press on."]

 [:p "Now pretend we want to add a third column of data: a unique ID for each
 name+color. Every pairwise combination of name, color, and ID is unique."]

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

 [:p "Only rose is red. Only orange is linked to ID 102, etc."]

 [:p "Our previous tactic of using a hash-map won't work. A hash-map only
 supports one-to-one relationships. We could get there with some gymnastics.
 Let's stuff some hash-maps into a vector."]

 [:pre
  (print-form-then-eval
   "(def flowers-2 [{:name \"rose\" :color :red :id 101}
                 {:name \"hibiscus\" :color :orange :id 102}
                 {:name \"sunflower\" :Color :yellow :id 103}])")]

 [:p#naive "We walk through the vector with "
  [:code "some"]
  " which returns the first logical "
  [:code "true"]
  " element."]

 [:pre
  [:code ";; What flower is red?"]
  [:br]
  (print-form-then-eval "(:name (some #(when (= :red (% :color)) %) flowers-2))")
  [:br]
  [:br]
  [:code ";; What is the ID of the orange flower?"]
  [:br]
  (print-form-then-eval "(:id (some #(when (= :orange (% :color)) %) flowers-2))")]

 [:p "But we're really starting to bump into issues. It's not terribly efficient
 to walk through the data each query. The invocation pattern is a bit wordy and
 perhaps not as readable as we'd hope."]

 [:p "And there's no enforcement of consistency. Notice, I made a keyboarding
 error. "
  [:code ":Color"]
  " in the last row should be "
  [:code ":color"]
  " with lower case 'c' to match the others. This error in the data makes our
 otherwise valid query fail."]

 [:pre
  (print-form-then-eval "(:name (some #(when (= :yellow (% :color)) %) flowers-2))")]

 [:p "We fail to find a match because of the inconsistent `:Color` entry in
 `flowers-2`"]

 [:p
  [:strong
   "The One-see library supplies a lightweight solution to arranging data with
 enforced "
   [:a {:href "#one-to-one"} "symmetric one-to-one"]
   " relationships and provides efficient look-up with a streamlined
 invocation pattern."]]]

