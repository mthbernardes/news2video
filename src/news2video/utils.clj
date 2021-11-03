(ns news2video.utils
  (:require [clojure.java.io :as io])
  (:import (java.io File)))

(defn rand-str []
  (apply str (take 10 (repeatedly #(char (+ (rand 26) 65))))))

(defn data->disk! [data ^String filename]
  (io/copy data (File. filename)))