(ns news2video.feed.g1
  (:require [remus :refer [parse-url]]))

(defn ^:private image-from-entry [entry]
  (-> entry
      (get-in [:extra :content])
      first
      (get-in [:attrs :url])))

(defn get! []
  (->> "https://g1.globo.com/rss/g1/planeta-bizarro/"
       parse-url
       :feed
       :entries
       (mapv (fn [{:keys [title] :as entry}]
               {:transcript title
                :image      {:url (image-from-entry entry)}}))
       (filter (comp :url :image))))
