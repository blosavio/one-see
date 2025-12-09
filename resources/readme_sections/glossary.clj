[:section#glossary

 [:h2 "Glossary"]

 [:p "Refer to this chart for the following definitions."]

 [:table
  [:tr
   [:th "flower"]
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

 [:dl
  [:dt#column "column"]
  [:dd
   [:p "A vertical chuck of related data within a category. Concretely, the
 header labels of a table ("
    [:em "flower"]
    ", "
    [:em "color"]
    ", and "
    [:em "ID"]
    " above), or the keys of a hash-map, or the fields of a record refer to a
 column. "
    [:em "Rose"]
    ", "
    [:em "hibiscus"]
    ", and "
    [:em "sunflower"]
    " make up a column of "
    [:em "flower"]
    " data."]]

  [:dt#one-to-one "symmetrical one-to-one"]
  [:dd
   [:p [:code "One-see"]
    "'s everyday term for "
    [:a {:href "https://en.wikipedia.org/wiki/Bijection"}
     "bijection"]
    "."]

   [:p "Practically, a condition imposed on an aggregate of data rows (e.g., a "
    [:a {:href "#table"} "table"]
    "), such that a value in one row is unique among the corresponding values in
 all the other rows. Thus, a row may be unambiguously located by searching for a
 particular value in a particular column."]

   [:p "A hash-map provides a uni-directional one-to-one relationship: Given
 one "
    [:em "key"]
    " unique among its peers, a hash-map returns one value. A hash-map does not
 guarantee that given one value, it will return one key. A "
    [:em "LookUp"]
    " instance provides a symmetrical one-to-one relationship: Given any key, a
 LookUp returns exactly one value and given that value, returns the
 corresponding key."]]

  [:dt#row "row"]
  [:dd
   [:p "A horizontal chunk of related data (e.g., "
    [:em "rose"]
    ", "
    [:em "red"]
    ", and "
    [:em "101"]
    " above). Each value is unique among its siblings in other rows. The
 keys/fields are identical between rows, i.e., "
    [:em "hibiscus"]
    " in the "
    [:em "flower"]
    " column of row 2 above is unique from the other values in that column."]]

  [:dt#table "table"]
  [:dd
   [:p "A sequence of "
    [:a {:href "#row"} "rows"]
    " that maintains a guarantee that the values of each row is unique within
 their respective columns."]]]]

