(ns one-see.core
  "A lightweight utility for establishing and querying one-to-one-to-one-to-one
  relationships.")


(defn
  valid-fields?
  "Returns `true` if fields of hashmap/record `m` exactly match those of the
  other elements in `x`.

  Returns `true` if `x` is empty. Returns `false` if `m` is empty."
  {:UUIDv4 #uuid "4529e709-29ef-463c-bbc7-07729b0fea2d"
   :no-doc true}
  [x m]
  (cond
    (empty? m) false
    (empty? x) true
    :else (= (set (keys (first x)))
             (set (keys m)))))


(defn unique-values?
  "Returns `true` if, for each field, the values of hashmap/record `m` are
  unique from all corresponding values in the other elements of `x`.

  Assumes fields of `m` exactly match those of the elements of `x`. See also
  [[valid-fields?]].

  Returns `true` if `x` is empty."
  {:UUIDv4 #uuid "f79704bf-ad0f-4841-9cfd-bd86ef9850c1"
   :no-doc true}
  [x m]
  (if (empty? x)
    true
    (let [ks (keys (first x))
          vs (reduce #(assoc %1 %2 (set (map %2 x))) {} ks)]
      (every? #(nil? ((get vs %1) (get m %1))) ks))))


(defprotocol RowStack
  "Operations for manipulating a sequential of hashmaps/records. Every `push`-ed
  hashmap/record:

  1. Must include exactly the keys/fields of existing elements, if any, and
  2. The value of each field must be unique from those of the other existing
  elements.

  Instances implement the `invoke` methods of `clojure.lang.IFn` with
  [[get-val]].

  Sample data for following examples.
  ```clojure
  (defrecord Flower [flower color id])

  (def flowers (stack-rows [[\"rose\" :red 101]
                            [\"hibiscus\" :orange 102]
                            [\"sunflower\" :yellow 103]]
                           ->Flower))
  ```"

  (all [this]
    "Returns all rows of the internal representation of a `RowStack` instance, a
 plain Clojure sequential of hashmaps/records.

  Note: The returned sequential will not maintain a `RowStack` instance's
  uniqueness guarantees.

  Example:
  ```clojure
  (all flowers)
  ;; => [#one_see.core.Flower{:flower \"rose\", :color :red, :id 101}
  ;;     #one_see.core.Flower{:flower \"hibiscus\", :color :orange, :id 102}
  ;;     #one_see.core.Flower{:flower \"sunflower\", :color :yellow, :id 103}]
  ```")

  (push [this m]
    "Returns a new `RowStack` instance with appended hashmap/record
  `m` if and only if the fields exactly match and the values are unique from all
  other rows. Otherwise, throws an exception.

  Prefer the higher-level [[stack-rows]] for constructing an instance.

  Example:
  ```clojure
  (all (push flowers (->Flower \"daffodil\" :white 104)))
  ;; [#one_see.core.Flower{:flower \"rose\", :color :red, :id 101}
  ;;  #one_see.core.Flower{:flower \"hibiscus\", :color :orange, :id 102}
  ;;  #one_see.core.Flower{:flower \"sunflower\", :color :yellow, :id 103}
  ;;  #one_see.core.Flower{:flower \"daffodil\", :color :white, :id 104}]
  ```")

  (delete [this m]
    "Returns a new `RowStack` instance with hashmap/record `m` removed.

  Example:
  ```clojure
  (all (delete flowers (->Flower \"hibiscus\" :orange 102)))
  ;; => [#one_see.core.Flower{:flower \"rose\", :color :red, :id 101}
  ;;     #one_see.core.Flower{:flower \"sunflower\", :color :yellow, :id 103}]
  ```")

  (get-row
    [this field value]
    [this field value not-found]
    "Returns the row, a hashmap/record, for which `field` is associated to
 `value`. If none match, returns `nil` or `not-found`.

  Example:
  ```clojure
  (get-row flowers :id 103)
  ;; => #one_see.core.Flower{:flower \"sunflower\", :color :yellow, :id 103}
  ```")

  (get-val
    [this field-1 value-1 field-2]
    [this field-1 value-1 field-2 not-found]
    "From the row whose `field-1` is `value-1`, returns the value associated to
  `field-2`.

  Example, explicit invocation:
  ```clojure
  (get-val flowers :color :orange :flower)
  ;; => \"hibiscus\"
  ```

 `get-val` supplies the `invoke` method, so a `RowStack` instance in the
 function postion consumes this argument sequence.

  Example, implicit invocation:
  ```clojure
  (flowers :color :orange :flower)
  ;; => \"hibiscus\"
  ```")

  (size [this]
    "Returns an array-map of a `RowStack` instance's row and column count as
  `{:n-rows <integer> :n-cols <integer>}`.

  Example:
  ```clojure
  (size flowers)
  ;; => {:n-rows 3, :n-cols 3}
  ```"))


(declare stack)


(defn delete-helper
  "Returns a new `RowStack` with `match` removed."
  {:UUIDv4 #uuid "6bcf2260-b1b6-42f8-ac54-8ed81ac5b86d"
   :no-doc true}
  [x match]
  (reduce #(if-not (= %2 match)
             (push %1 %2)
             %1)
          (stack)
          x))


(defn get-helper
  "Given sequential `s` of hashmaps, returns the unique entry whose key `k` is
  value `v`. If none match, returns `nil` or `not-found`."
  {:UUIDv4 #uuid "21b39173-032d-4053-811b-3f30184641ce"
   :no-doc true}
  ([s k v] (get-helper s k v nil))
  ([s k v not-found] (or (some #(when (= (get % k) v) %) s)
                         not-found)))


(defn stack
  "Returns a `RowStack` instance, empty if `x` is not supllied."
  {:UUIDv4 #uuid "2c87a1be-e218-4125-8be8-47d92ad1afb5"
   :no-doc true}
  ([] (stack []))
  ([x]
   (reify
     RowStack
     (all [_] x)
     (push [_ m] (cond
                   (not (valid-fields? x m)) (throw (Exception. "Fields of hashmap/record must exactly match those of the other entries."))
                   (not (unique-values? x m)) (throw (Exception. "Values must be unique from those of other entries."))
                   :else (stack (conj x m))))
     (delete [_ m] (delete-helper x m))
     (get-row [_ ky vl] (get-helper x ky vl))
     (get-row [_ ky vl not-found] (get-helper x ky vl not-found))
     (get-val [_ ky vl trgt] (get (get-helper x ky vl) trgt))
     (get-val [_ ky vl trgt not-found] (if-let [found (get-helper x ky vl)]
                                         (get found trgt)
                                         not-found))
     (size [_] {:n-rows (count x)
                :n-cols (count (first x))})

     clojure.lang.IFn
     (invoke [this ky vl trgt] (.get-val this ky vl trgt))
     (invoke [this ky vl trgt not-found] (.get-val this ky vl trgt not-found))

     Object
     (toString [_] (str x)))))


(defn stack-rows
  "Returns a `RowStack` instance. If given only a sequential `s` of hashmaps,
  directly [[push]]-es the elements of `s`. If given a sequential `s` of field
  values and a record constructor `->rec`, invokes the constructor with the
  elements of `s` before `push`-ing.

  Throws an exception if key/fields don't exactly match or if a value is not
  unique within its column.

  Example, 1-arity consuming hash-maps:
  ```clojure
  (stack-rows [{:flower \"rose\" :color :red :id 101}
               {:flower \"hibiscus\" :color :orange :id 102}
               {:flower \"sunflower\" :color :yellow :id 103}])
  ```

  Example, 2-arity consuming records:
  ```clojure
  (defrecord flower-color [flower color id])

  (stack-rows [[\"rose\" :red 101]
               [\"hibiscus\" :orange 102]
               [\"sunflower\" :yellow 103]] ->flower-color)
  ```"
  {:UUIDv4 #uuid "0cb1846e-35ec-455b-b514-b23044d8e7be"}
  ([s]
   (reduce #(push %1 %2) (stack) s))
  ([s ->rec]
   (reduce #(push %1 (apply ->rec %2)) (stack) s)))

