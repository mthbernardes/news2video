(ns news2video.diplomat.video
  (:require [clojure.java.shell :as shell]
            [news2video.utils :as utils]
            [clojure.string :as string]))

(defn create! [audio-filename image-filename]
  (let [video-filename (format "/tmp/%s.mkv" (utils/rand-str))
        command ["ffmpeg" "-loop" "1" "-framerate" "1" "-i" image-filename "-i" audio-filename
                 "-c:v" "libx264" "-preset" "veryslow" "-crf" "0"
                 "-c:a" "copy" "-shortest" video-filename]
        execution (apply shell/sh command)]
    (when (-> execution :exit zero?)
      video-filename)))

(defn ^:private generate-videos-to-merge [filenames]
  (let [filename "/tmp/videos-to-merge"]
    (->> filenames
         (filter identity)
         (mapv #(format "file '%s'" %))
         (string/join "\n")
         (spit filename))
    filename))

(defn merge! [videos]
  (let [output-video "/tmp/output-video.mkv"
        videos-to-merge (generate-videos-to-merge videos)
        command ["ffmpeg" "-f" "concat" "-safe" "0" "-i" videos-to-merge "-vcodec" "libx264" "-crf" "28" "-preset" "faster" "-tune" "film" "-c" "copy" output-video]
        execution (apply shell/sh command)]
    (if (-> execution :exit zero?)
      output-video
      (throw  (Exception. ^String (:err execution))))))
