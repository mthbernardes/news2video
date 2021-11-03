(ns news2video.main
  (:require [news2video.feed.g1 :as g1]
            [news2video.diplomat.image :as image]
            [news2video.diplomat.tts :as tts]
            [news2video.diplomat.video :as video]))

(defn ^:private get-news []
  (g1/get!))

(defn ^:private get-image! [{{:keys [url]} :image :as news-entry}]
  (let [image-filename (image/download! url)]
    (assoc-in news-entry [:image :filename] image-filename)))

(defn ^:private add-breaking-news-announcement [news-feed]
  (let [announcement {:transcript "Boa noite confira agora as ultimas loucuras que aconteceram nesse Brasil de meu Deus."
                      :image      {:filename "resources/bancada.png"}}]
    (conj news-feed announcement)))

(defn ^:private transcript->audio [{:keys [transcript] :as news-entry}]
  (let [audio-filename (tts/transform! transcript)]
    (assoc-in news-entry [:audio :filename] audio-filename)))

(defn ^:private add-breaking-news-intro [news-feed]
  (let [intro {:audio {:filename "resources/vinheta.mp3"}
                      :image {:filename "resources/logo.png"}}]
    (conj news-feed intro)))

(defn ^:private news-video [{:keys [audio image] :as new-entry}]
  (let [audio-filename (:filename audio)
        image-filename (:filename image)
        video-filename (video/create! audio-filename image-filename)]
    (assoc new-entry :video video-filename)))

(defn compile-videos [news-feed]
  (let [video-filename (->> news-feed (map :video) video/merge!)]
    {:news-feed news-feed :video video-filename}))

(defn -main []
  (->> (get-news)
       (pmap get-image!)
       add-breaking-news-announcement
       (pmap transcript->audio)
       add-breaking-news-intro
       (pmap news-video)
       compile-videos
       :video
       println)
  (shutdown-agents))