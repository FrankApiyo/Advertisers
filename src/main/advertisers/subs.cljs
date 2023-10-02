(ns advertisers.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::loading?
 (fn [db]
   (or (:loading?  db)
       (:stats-loading?  db))))

(re-frame/reg-sub
 ::advertisers
 (fn [db]
   (:advertisers db)))

(re-frame/reg-sub
 ::statistics
 (fn [db]
   (:statistics db)))

(re-frame/reg-sub
 ::error-state?
 (fn [db]
   (or
    (:error-state? db)
    (:stats-error-state? db))))

(re-frame/reg-sub
 ::add-statistics
 (fn [db]
   (:add-statistics db)))

(re-frame/reg-sub
 ::enriched-advertisers
 :<- [::advertisers]
 :<- [::statistics]
 (fn [[advertisers stats]]
   (mapv
    (fn [{:keys [id] :as advertiser}]
      (let [stats-for-id (first (filter #(= id (:advertiserId %))
                                        stats))]
        (merge stats-for-id advertiser)))
    advertisers)))
