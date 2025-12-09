(ns one-see.scratch-tests
  (:require
   [clojure.test :refer [are
                         deftest
                         is
                         run-test
                         run-tests
                         testing]]
   [one-see.scratch :refer :all]))


(defrecord Flower [flower color id leaves])


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
      {:flower {"rose" #one_see.scratch_tests.Flower{:flower "rose", :color :red, :id 101, :leaves :pinnate}},
       :color {:red #one_see.scratch_tests.Flower{:flower "rose", :color :red, :id 101, :leaves :pinnate}},
       :id {101 #one_see.scratch_tests.Flower{:flower "rose", :color :red, :id 101, :leaves :pinnate}},
       :leaves {:pinnate #one_see.scratch_tests.Flower{:flower "rose", :color :red, :id 101, :leaves :pinnate}}}))
  (testing "illegal assoc, failure"
    (is (thrown?
         Exception
         (assoc-row (->Flower "rose" :red 101 :pinnate) {:color {:red 99}})))))


(deftest expand-tests
  (are [x y] (= x y)
    (expand flowers)
    {:flower {"rose" #one_see.scratch_tests.Flower{:flower
                                                   "rose",
                                                   :color :red,
                                                   :id 101, :leaves :pinnate},
              "hibiscus" #one_see.scratch_tests.Flower{:flower "hibiscus",
                                                       :color :orange,
                                                       :id 102,
                                                       :leaves :lanceolate},
              "sunflower" #one_see.scratch_tests.Flower{:flower "sunflower",
                                                        :color :yellow,
                                                        :id 103,
                                                        :leaves :cardioid}},
     :color {:red #one_see.scratch_tests.Flower{:flower "rose",
                                                :color :red,
                                                :id 101,
                                                :leaves :pinnate},
             :orange #one_see.scratch_tests.Flower{:flower "hibiscus",
                                                   :color :orange,
                                                   :id 102,
                                                   :leaves :lanceolate},
             :yellow #one_see.scratch_tests.Flower{:flower "sunflower",
                                                   :color :yellow,
                                                   :id 103,
                                                   :leaves :cardioid}},
     :id {101 #one_see.scratch_tests.Flower{:flower "rose",
                                            :color :red,
                                            :id 101,
                                            :leaves :pinnate},
          102 #one_see.scratch_tests.Flower{:flower "hibiscus",
                                            :color :orange,
                                            :id 102,
                                            :leaves :lanceolate},
          103 #one_see.scratch_tests.Flower{:flower "sunflower",
                                            :color :yellow,
                                            :id 103,
                                            :leaves :cardioid}},
     :leaves {:pinnate #one_see.scratch_tests.Flower{:flower "rose",
                                                     :color :red,
                                                     :id 101,
                                                     :leaves :pinnate},
              :lanceolate #one_see.scratch_tests.Flower{:flower "hibiscus",
                                                        :color :orange,
                                                        :id 102,
                                                        :leaves :lanceolate},
              :cardioid #one_see.scratch_tests.Flower{:flower "sunflower",
                                                      :color :yellow,
                                                      :id 103,
                                                      :leaves :cardioid}}}))


(def flower-lookup (look-up flowers))


(deftest LookUp-tests
  (testing "get-row, successful"
    (are [x y] (= x y)
      (get-row flower-lookup :flower "rose")
      #one_see.scratch_tests.Flower{:flower "rose", :color :red, :id 101, :leaves :pinnate}))

  (testing "get-row, not-found"
    (are [x y] (= x y)
      nil (get-row flower-lookup :flower "daisy")
      :nope! (get-row flower-lookup :flower "daisy" :nope!)))

  (testing "get-val, successful"
    (are [x y] (= x y)
      101 (get-val flower-lookup :flower "rose" :id)
      101 (flower-lookup :flower "rose" :id)))

  (testing "get-val, not-found"
    (are [x y] (= x y)
      nil (flower-lookup :flower "daisy" :id)
      nil (flower-lookup :flower "rose" :habitat)

      :nope! (flower-lookup :flower "daisy" :id :nope!)
      :nope! (flower-lookup :flower "rose" :habitat :nope!)))

  (testing "auxilliary methods"
    (are [x y] (= x y)
      (table flower-lookup)
      [#one_see.scratch_tests.Flower{:flower "rose", :color :red, :id 101, :leaves :pinnate}
       #one_see.scratch_tests.Flower{:flower "hibiscus", :color :orange, :id 102, :leaves :lanceolate}
       #one_see.scratch_tests.Flower{:flower "sunflower", :color :yellow, :id 103, :leaves :cardioid}]

      (size flower-lookup)
      {:n-rows 3, :n-cols 4}

      (str flower-lookup)
      "[#one_see.scratch_tests.Flower{:flower \"rose\", :color :red, :id 101, :leaves :pinnate} #one_see.scratch_tests.Flower{:flower \"hibiscus\", :color :orange, :id 102, :leaves :lanceolate} #one_see.scratch_tests.Flower{:flower \"sunflower\", :color :yellow, :id 103, :leaves :cardioid}]")))


#_(run-tests)

