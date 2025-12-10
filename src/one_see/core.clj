(ns one-see.core
  "A lightweight utility for establishing and querying symmetric one-to-one
  relationships.")


(defn assoc-in-iff
  "Associate as with `assoc-in`, but if and only if the path destination is
  unoccupied. If occupied, throws."
  {:UUIDv4 #uuid "cfedc33e-cfe3-425c-91c6-02aa6ed78bb7"
   :no-doc true}
  [m ks v]
  (let [err-str (fn [v ks found] (str "Illegal attempt to associate new value: " v " at path: " ks " (existing value: " found ")"))
        not-found (gensym)
        found (get-in m ks not-found)]
    (if (= not-found found)
      (assoc-in m ks v)
      (throw (Exception. (err-str v ks found))))))


(defn assoc-row
  "Given a `row`, a hash-map/record of one-to-one data, associates all key-val
  permutations into the expansion `ex`."
  {:UUIDv4 #uuid "d8021f68-02a7-41e1-a557-eb419dbaba7a"
   :no-doc true}
  [row ex]
  (reduce-kv (fn [acc k v] (assoc-in-iff acc [k v] row)) ex row))


(defn expand
  "Given a sequential `s` of hash-maps/records, all with identical fields,
  returns nested hash-map such that any row may be located with exactly two
  lookups, without walking the sequential. Throws if any value is not unique
  within its column.

  Relies on Clojure's structural sharing for efficient space usage."
  {:UUIDv4 #uuid "3d5f7e28-c5af-499c-bb3d-2bc4a899565d"
   :no-doc true}
  [s]
  (reduce-kv (fn [acc idx m] (assoc-row m acc)) {} s))


(defprotocol LookUp
  "Methods for arranging symmetric one-to-one relationships and performing
  efficient look ups.

  Use [[look-up]] for creating an instance. Instances implement the `invoke`
  methods of `clojure.lang.IFn` with [[get-val]].

  Sample data for the following examples:

  ```clojure
  (defrecord Flower [name color id])

  (def flowers (look-up [[\"rose\" :red 101]
                         [\"hibiscus\" :orange 102]
                         [\"sunflower\" :yellow 103]]
                        ->Flower))
  ```"

  (table [this]
    "Returns the original data in tabular form, as provided to [[look-up]].

  Example:
  ```clojure
  (table flowers)
  ```")

  (expansion [this]
    "Returns a hash-map of the expanded key-vals which enable efficient look
  ups.

  Example:
  ```clojure
  (expansion flowers)
  ```")

  (get-row
    [this col val]
    [this col val not-found]
    "Returns the row containing value `val` located in column `col`.

  Example query: *Return the row whose `:name` is `\"rose\"`.*
  ```clojure
  (get-row flowers :name \"rose\")
  ;; => #one_see.Flower{:name \"rose\", :color :red, :id 101}
  ```")

  (get-val
    [this col-1 val-1 col-2]
    [this col-1 val-1 col-2 not-found]
    "Efficiently finds the row whose `val-1` is located in column `col-1`, then
 returns `val-2` located in `col-2` of that row. If not found, returns `nil` or
 `not-found`.

  `get-val` provides the `invoke` implementation, so a `LookUp` instance in the
  function position delegates to `get-val`.

  Example query: *What is the `:id` of the row whose `:name` is `\"rose\"`?*

  Example, explicit invocation:
  ```clojure
  (get-val flowers :name \"rose\" :id)
  ;; => 101
  ```

  Example, implicit invocation:
  ```clojure
  (flowers :name \"rose\" :id)
  ;; => 101
  ```")

  (size
    [this]
    "Returns an array-map of a `LookUp` instance's row and column counts.

  Example:
  ```clojure
  (size flowers)
  ;; => {:n-rows 3, :n-cols 3}
  ```"))


(defn make-look-up
  "Returns an `LookUp` instance. `tabled` is the sequence of hash-maps/records.
  `expanded` is the re-arranged data optimized for fast lookups, as produced by
  [[expand]]."
  {:UUIDv4 #uuid "7e8ee9c2-afdc-4cbc-b1ae-fd8e3b3509b7"
   :no-doc true}
  [tabled expanded]
  (reify
    LookUp
    (table [this] tabled)
    (expansion [this] expanded)

    (get-row [this key-1 val-1] (get-in expanded [key-1 val-1]))
    (get-row [this key-1 val-1 not-found] (get-in expanded [key-1 val-1] not-found))

    (get-val [this key-1 val-1 key-2] (get-in expanded [key-1 val-1 key-2]))
    (get-val [this key-1 val-1 key-2 not-found] (get-in expanded [key-1 val-1 key-2] not-found))

    (size [this] {:n-rows (count tabled)
                  :n-cols (count (first tabled))})

    clojure.lang.IFn
    (invoke [this key-1 value-1 key-2] (.get-val this key-1 value-1 key-2))
    (invoke [this key-1 value-1 key-2 not-found] (.get-val this key-1 value-1 key-2 not-found))

    Object
    (toString [this] (str tabled))))


(defn look-up
  "Establishes symmetric one-to-one relationships and returns a [[LookUp]]
  instance which provides efficient look ups.

  A lone `s` is a sequential of hash-maps. If given a sequential `s` of field
  values and a record constructor `->rec`, invokes the constructor with the
  elements of `s`. Prefer this later, two-arity version.

  Throws an exception:

  1. If key/fields don't exactly match, or
  2. If a value is not unique within its column.

  Example, 1-arity consuming a sequence of hash-maps:
  ```clojure
  (look-up [{:name \"rose\" :color :red :id 101}
            {:name \"hibiscus\" :color :orange :id 102}
            {:name \"sunflower\" :color :sunflower :id 103}])
  ```

  Example, 2-arity consuming a sequence of field values plus a record
  constructor (preferred):
  ```clojure
  (defrecord Flower [flower color id])

  (look-up [[\"rose\" :red 101]
            [\"hibiscus\" :orange 102]
            [\"sunflower\" :yellow 103]]
           ->Flower)
  ```"
  {:UUIDv4 #uuid "9820169c-a41b-4012-b937-5c9d5afbf92c"}
  ([s] (make-look-up s (expand s)))
  ([s ->rec] (let [applied (mapv #(apply ->rec %) s)]
               (make-look-up applied (expand applied)))))

