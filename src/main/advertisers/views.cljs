(ns advertisers.views)

(defn table []
  [:table {:class "bg-[#222] border-collapse border border-slate-500 min-w-screen-90 m-5 text-justify border-collapse"}
   [:thead
    [:tr
     [:th {:class "border border-[#343434]-600 text-justify"} "State"]
     [:th {:class "border border-[#343434]-600 text-justify"} "City"]]]
   [:tbody
    [:tr
     [:td {:class "border border-[#343434]-700"} "Indiana"]
     [:td {:class "border border-[#343434]-700"} "Indianapolis"]]
    [:tr
     [:td {:class "border border-[#343434]-700"} "Ohio"]
     [:td {:class "border border-[#343434]-700"} "Columbus"]]
    [:tr
     [:td {:class "border border-[#343434]-700"} "Michigan"]
     [:td {:class "border border-[#343434]-700"} "Detroit"]]]])

(defn overview []
  [:div {:class "bg-[#313131] text-white"}
   [:div {:class "flex"}
    [table]]])
