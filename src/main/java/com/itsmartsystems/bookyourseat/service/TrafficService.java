package com.itsmartsystems.bookyourseat.service;

        import com.itsmartsystems.bookyourseat.dto.RoutesOption;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.stereotype.Service;
        import org.springframework.web.client.RestClient;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Set;

        @Service
        public class TrafficService {


        // coordonatele firmei

        @Value("${office.latitude}")
        private double latDest ;

        @Value("${office.longitude}")
        private double longDest ;


        @Value("${google.maps.api.key}")
        private String apiKey ;

        @Value("${google.maps.routes.url}")
        private String mapsURL;

        private final RestClient restClient ;

        // Modurile de deplasare acceptate de Google Routes API
        private static final Set<String> VALID_TRAVEL_MODES = Set.of(
                "DRIVE", "WALK", "BICYCLE", "TRANSIT", "TWO_WHEELER");

        // routingPreference (TRAFFIC_AWARE) e acceptat DOAR de DRIVE si TWO_WHEELER;
        // pentru celelalte moduri, Google API respinge cererea daca e prezent.
        private static final Set<String> MODES_SUPPORTING_ROUTING_PREFERENCE = Set.of(
                "DRIVE", "TWO_WHEELER");

        // computeAlternativeRoutes NU e suportat pentru TRANSIT.
        private static final Set<String> MODES_SUPPORTING_ALTERNATIVE_ROUTES = Set.of(
                "DRIVE", "WALK", "BICYCLE", "TWO_WHEELER");

        public TrafficService(RestClient restClient)
        {
        this.restClient = restClient;
            }

        public List<RoutesOption> getTrafficRoutes(double latOrigin, double longOrigin , String metodaDeplasare) {

        // Normalizam si validam modul de deplasare primit (WALK, BICYCLE, TRANSIT, TWO_WHEELER, DRIVE)
        String travelMode = metodaDeplasare != null ? metodaDeplasare.trim().toUpperCase() : null;
        if (travelMode == null || !VALID_TRAVEL_MODES.contains(travelMode)) {
        throw new IllegalArgumentException(
        "Metoda de deplasare invalida: " + metodaDeplasare
        + ". Valorile acceptate sunt: " + VALID_TRAVEL_MODES);
                }

        // am creat structura map urilor pentru json - > origin
        /*
                "origin":{
                    "location":{
                    "latLng":{
                        "latitude": 37.419734,
                        "longitude": -122.0827784
                            }
                        }
                    }
                */
        HashMap<String , Object> latLng = new HashMap<>();
        latLng.put("latitude",latOrigin);
        latLng.put("longitude",longOrigin);
        HashMap<String , Object> location = new HashMap<>();
        location.put("latLng" , latLng);
        HashMap<String , Object> origin = new HashMap<>();
        origin.put("location",location);


        /*
                "destination":{
                    "location":{
                    "latLng":{
                        "latitude": 37.417670,
                        "longitude": -122.079595
                    }
                    }
                },
                */
        HashMap<String , Object> latLngDest = new HashMap<>();
        latLngDest.put("latitude",latDest);
        latLngDest.put("longitude",longDest);

        HashMap<String , Object> locationDest = new HashMap<>();
        locationDest.put("latLng" , latLngDest);

        HashMap<String , Object> originDest = new HashMap<>();
        originDest.put("location",locationDest);

        HashMap<String,Object> requestBody = new HashMap<>();
        requestBody.put("origin" , origin);
        requestBody.put("destination" , originDest);
        requestBody.put("travelMode" , travelMode);

        // routingPreference e valid DOAR pentru DRIVE si TWO_WHEELER.
        // Pentru WALK, BICYCLE si TRANSIT, Google API respinge cererea daca e prezent,
        // asa ca il adaugam doar cand modul chiar il suporta.
        if (MODES_SUPPORTING_ROUTING_PREFERENCE.contains(travelMode)) {
        requestBody.put("routingPreference", "TRAFFIC_AWARE");
                }

        // computeAlternativeRoutes nu e suportat pentru TRANSIT; il adaugam doar
        // pentru modurile care il accepta, ca sa evitam eroarea de la Google.
        if (MODES_SUPPORTING_ALTERNATIVE_ROUTES.contains(travelMode)) {
        requestBody.put("computeAlternativeRoutes", true);
                }

        // Trimitem cererea (HEADERS + BODY, direct in lant, cu RestClient)
        Map<String, Object> response = restClient.post()
                        .uri(mapsURL)
                        .header("X-Goog-Api-Key", apiKey)
                        .header("X-Goog-FieldMask", "routes.duration,routes.distanceMeters")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class);

        // Primim raspunsul
        List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");

        if (routes == null) {
        return new ArrayList<>();
                }

        // creez o lista care sa contina toate rutele
        List<RoutesOption> result = new ArrayList<>();
        for(Map<String , Object> route : routes)
                {
        Integer distanceM = (Integer) route.get("distanceMeters");
        String durationS= (String) route.get("duration");
        long duration = Long.parseLong(durationS.replace("s",""));

        RoutesOption option = new RoutesOption("Ruta " + (result.size() + 1) , distanceM , duration);
        result.add(option);
                }
        return result ;
            }
        }