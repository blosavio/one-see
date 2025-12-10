(ns one-see.core-tests
  (:require
   [clojure.test :refer [are
                         deftest
                         is
                         run-test
                         run-tests
                         testing]]
   [one-see.core :refer :all]))


(defrecord Flower [name color id leaves])


(def flowers [(->Flower "rose" :red 101 :pinnate)
              (->Flower "hibiscus" :orange 102 :lanceolate)
              (->Flower "sunflower" :yellow 103 :cardioid)])


(deftest assoc-in-iff-tests
  (testing "successful assoc"
    (are [x y] (= x y)
      (assoc-in-iff {:a {:b {:c 99}}} [:a :b :d] 101)
      {:a {:b {:c 99 :d 101}}}))
  (testing "illegal assoc"
    (is (thrown? Exception (assoc-in-iff {:a {:b {:c 99}}} [:a :b :c] 101)))))


(deftest assoc-row-tests
  (testing "successful assoc"
    (are [x y] (= x y)
      (assoc-row (->Flower "rose" :red 101 :pinnate) {})
      {:name {"rose" #one_see.core_tests.Flower{:name "rose", :color :red, :id 101, :leaves :pinnate}},
       :color {:red #one_see.core_tests.Flower{:name "rose", :color :red, :id 101, :leaves :pinnate}},
       :id {101 #one_see.core_tests.Flower{:name "rose", :color :red, :id 101, :leaves :pinnate}},
       :leaves {:pinnate #one_see.core_tests.Flower{:name "rose", :color :red, :id 101, :leaves :pinnate}}}))
  (testing "illegal assoc, failure"
    (is (thrown?
         Exception
         (assoc-row (->Flower "rose" :red 101 :pinnate) {:color {:red 99}})))))


(deftest expand-tests
  (are [x y] (= x y)
    (expand flowers)
    {:name {"rose" #one_see.core_tests.Flower{:name "rose",
                                              :color :red,
                                              :id 101,
                                              :leaves :pinnate},
            "hibiscus" #one_see.core_tests.Flower{:name "hibiscus",
                                                  :color :orange,
                                                  :id 102,
                                                  :leaves :lanceolate},
            "sunflower" #one_see.core_tests.Flower{:name "sunflower",
                                                   :color :yellow,
                                                   :id 103,
                                                   :leaves :cardioid}},
     :color {:red #one_see.core_tests.Flower{:name "rose",
                                             :color :red,
                                             :id 101,
                                             :leaves :pinnate},
             :orange #one_see.core_tests.Flower{:name "hibiscus",
                                                :color :orange,
                                                :id 102,
                                                :leaves :lanceolate},
             :yellow #one_see.core_tests.Flower{:name "sunflower",
                                                :color :yellow,
                                                :id 103,
                                                :leaves :cardioid}},
     :id {101 #one_see.core_tests.Flower{:name "rose",
                                         :color :red,
                                         :id 101,
                                         :leaves :pinnate},
          102 #one_see.core_tests.Flower{:name "hibiscus",
                                         :color :orange,
                                         :id 102,
                                         :leaves :lanceolate},
          103 #one_see.core_tests.Flower{:name "sunflower",
                                         :color :yellow,
                                         :id 103,
                                         :leaves :cardioid}},
     :leaves {:pinnate #one_see.core_tests.Flower{:name "rose",
                                                  :color :red,
                                                  :id 101,
                                                  :leaves :pinnate},
              :lanceolate #one_see.core_tests.Flower{:name "hibiscus",
                                                     :color :orange,
                                                     :id 102,
                                                     :leaves :lanceolate},
              :cardioid #one_see.core_tests.Flower{:name "sunflower",
                                                   :color :yellow,
                                                   :id 103,
                                                   :leaves :cardioid}}}))


(def flower-lookup (look-up flowers))


(deftest LookUp-tests
  (testing "get-row, successful"
    (are [x y] (= x y)
      (get-row flower-lookup :name "rose")
      #one_see.core_tests.Flower{:name "rose", :color :red, :id 101, :leaves :pinnate}))

  (testing "get-row, not-found"
    (are [x y] (= x y)
      nil (get-row flower-lookup :name "daisy")
      :nope! (get-row flower-lookup :name "daisy" :nope!)))

  (testing "get-val, successful"
    (are [x y] (= x y)
      101 (get-val flower-lookup :name "rose" :id)
      101 (flower-lookup :name "rose" :id)))

  (testing "get-val, not-found"
    (are [x y] (= x y)
      nil (flower-lookup :name "daisy" :id)
      nil (flower-lookup :name "rose" :habitat)

      :nope! (flower-lookup :name "daisy" :id :nope!)
      :nope! (flower-lookup :name "rose" :habitat :nope!)))

  (testing "auxilliary methods"
    (are [x y] (= x y)
      (table flower-lookup)
      [#one_see.core_tests.Flower{:name "rose", :color :red, :id 101, :leaves :pinnate}
       #one_see.core_tests.Flower{:name "hibiscus", :color :orange, :id 102, :leaves :lanceolate}
       #one_see.core_tests.Flower{:name "sunflower", :color :yellow, :id 103, :leaves :cardioid}]

      (size flower-lookup)
      {:n-rows 3, :n-cols 4}

      (str flower-lookup)
      "[#one_see.core_tests.Flower{:name \"rose\", :color :red, :id 101, :leaves :pinnate} #one_see.core_tests.Flower{:name \"hibiscus\", :color :orange, :id 102, :leaves :lanceolate} #one_see.core_tests.Flower{:name \"sunflower\", :color :yellow, :id 103, :leaves :cardioid}]")))


#_(run-tests)

