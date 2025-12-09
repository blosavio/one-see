(ns one-see.core-test
  (:require [clojure.test :refer [are
                                  deftest
                                  is
                                  run-test
                                  run-tests
                                  testing]]
            [one-see.core :refer :all]))


(defrecord test-rec [a b c])


(def test-data [["rose"      :red     101]
                ["hibiscus"  :orange  102]
                ["sunflower" :yellow  103]])


(defrecord Flower [flower color id])


(def flowers (stack-rows test-data ->Flower))


(deftest valid-fields?-tests
  (are [x] (true? x)
    (valid-fields? []
                   {:a 11 :b 22 :c 33})

    (valid-fields? [{:a 97 :b 98 :c 99}]
                   {:a 11 :b 22 :c 33})

    (valid-fields? [{:a 97 :b 98 :c 99}]
                   (->test-rec 11 22 33)))

  (are [x] (false? x)
    (valid-fields? [{:a 97 :b 98}]
                   {:a 11 :b 22 :c 33})

    (valid-fields? [{:a 97 :b 98 :d 33}]
                   {:a 11 :b 22 :c 33})

    (valid-fields? [{:a 97 :b 98 :d 99}]
                   (->test-rec 11 22 33))

    (valid-fields? [{}] {})
    (valid-fields? [{}] {:a 11})
    (valid-fields? [{:a 11}] {})))


(deftest unique-values?-tests
  (are [x] (true? x)
    (unique-values? [{:a 97 :b 98 :c 99}]
                    {:a 11 :b 22 :c 33})

    (unique-values? [{:a 97 :b 98 :c 99}
                     {:a 87 :b 88 :c 89}
                     {:a 77 :b 78 :c 79}]
                    {:a 11 :b 22 :c 33})

    (unique-values? [{:a 97 :b 98 :c 99}]
                    (->test-rec 11 22 33))

    (unique-values? [(->test-rec 97 98 99)]
                    (->test-rec 11 22 33)))

  (are [x] (false? x)
    (unique-values? [{:a 97 :b 98 :c 99}]
                    {:a 11 :b 22 :c 99})))


(deftest delete-helper-tests
  (are [x y] (= x y)
    (all (delete-helper (all flowers) (->Flower "rose" :red 101)))
    [#one_see.core_test.Flower{:flower "hibiscus", :color :orange, :id 102}
     #one_see.core_test.Flower{:flower "sunflower", :color :yellow, :id 103}]))


(deftest get-helper-tests
  (are [x y] (= x y)
    (get-helper [{:a 11 :b 22 :c 33}
                 {:a 44 :b 44 :c 66}
                 {:a 77 :b 88 :c 99}] :b 88)
    {:a 77, :b 88, :c 99}

    (get-helper [{:a 11 :b 22 :c 33}
                 {:a 44 :b 44 :c 66}
                 {:a 77 :b 88 :c 99}] :b 999)
    nil

    (get-helper [{:a 11 :b 22 :c 33}
                 {:a 44 :b 44 :c 66}
                 {:a 77 :b 88 :c 99}] :b 999 :nope!)
    :nope!))


(deftest stack-rows-tests
  (are [x y] (= x y)
    (all (stack-rows test-data ->Flower))
    [#one_see.core_test.Flower{:flower "rose", :color :red, :id 101}
     #one_see.core_test.Flower{:flower "hibiscus", :color :orange, :id 102}
     #one_see.core_test.Flower{:flower "sunflower", :color :yellow, :id 103}]

    (all (stack-rows [{:foo 99 :bar \a :baz true}
                      {:foo 88 :bar \b :baz false}
                      {:foo 77 :bar \c :baz nil}]))
    [{:foo 99, :bar \a, :baz true}
     {:foo 88, :bar \b, :baz false}
     {:foo 77, :bar \c, :baz nil}]))


(deftest push-tests
  (testing "illegal `push`es"
    (is (thrown? Exception (push flowers {:flower "daffodil" :color "chartreuse" :id 99 :extra "foobar"})))
    (is (thrown? Exception (push flowers {:flower "daffodil" :color "chartreuse " :flavor "durian"})))
    (is (thrown? Exception (push flowers (->Flower "daffodil" :red 99))))))


(deftest get-tests
  (testing "successful `get`, `not-found` not supplied"
    (are [x y] (= x y)
      (get-row flowers :color :orange)
      (->Flower "hibiscus" :orange 102)

      (get-val flowers :color :orange :id)
      102))

  (testing "successful `get`, `not-found` supplied"
    (are [x y] (= x y)
      (get-row flowers :color :orange :nope!)
      (->Flower "hibiscus" :orange 102)

      (get-val flowers :color :orange :id :nope!)
      102))

  (testing "not found supplied"
    (are [x] (= x :nope!)
      (get-row flowers :color :chartreuse :nope!)
      (get-val flowers :color :chartreuse :id :nope!)))

  (testing "not found, default `nil`"
    (are [x] (nil? x)
      (get-row flowers :color "chartreuse")
      (get-val flowers :color "chartreuse" :id))))


(deftest delete-tests
  (are [x y] (= x y)
    (all (delete flowers (->Flower "hibiscus" :orange 102)))
    [#one_see.core_test.Flower{:flower "rose", :color :red, :id 101}
     #one_see.core_test.Flower{:flower "sunflower", :color :yellow, :id 103}]))


(deftest toString-tests
  (are [x y] (= x y)
    (str flowers)
    "[#one_see.core_test.Flower{:flower \"rose\", :color :red, :id 101} #one_see.core_test.Flower{:flower \"hibiscus\", :color :orange, :id 102} #one_see.core_test.Flower{:flower \"sunflower\", :color :yellow, :id 103}]"))


(deftest invoke-tests
  (are [x y] (= x y)
    :red (flowers :flower "rose" :color)
    :orange (flowers :flower "hibiscus" :color :nope!)
    :nope! (flowers :flower "daffodil" :color :nope!)))


(deftest size-tests
  (are [x y] (= x y)
    (size flowers)
    {:n-rows 3
     :n-cols 3}))


#_(run-tests)

