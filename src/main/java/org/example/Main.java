package org.example;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

enum Field{
    PRICE,
    STOCK_COUNT,
    PARTNER_CONTENT
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
class Offer {
    @JsonProperty("id")
    private String id;
    @JsonProperty("price")
    private int price;
    @JsonProperty("stock_count")
    private int stockCount;
    @JsonProperty("partner_content")
    private PartnerContent partnerContent;

    public Offer(String id, int price, int stockCount, String title, String description) {
        this.id = id;
        this.price = price;
        this.stockCount = stockCount;
        this.partnerContent = new PartnerContent(title, description);
    }

    public Offer(){
    }

    public List<Field> update(Offer newOffer){

        List<Field> updateFields = new LinkedList<>();

        if(this.price != newOffer.getPrice() && newOffer.getPrice() != 0){
            this.price = newOffer.getPrice();
            updateFields.add(Field.PRICE);
        }

        if(this.stockCount != newOffer.getStockCount() && newOffer.getStockCount() != 0){
            this.stockCount = newOffer.getStockCount();
            updateFields.add(Field.STOCK_COUNT);
        }

        if(this.getPartnerContent() == null){
            if (newOffer.getPartnerContent() != null) {
                this.partnerContent = newOffer.getPartnerContent();
                updateFields.add(Field.PARTNER_CONTENT);
            }
        }
        else {
            if (!Objects.equals(newOffer.getPartnerContent().description, this.partnerContent.description)
                    && newOffer.getPartnerContent().description != null) {
                this.partnerContent.description
                        = newOffer.getPartnerContent().description;
                updateFields.add(Field.PARTNER_CONTENT);
            }

            if (!Objects.equals(newOffer.getPartnerContent().title, this.partnerContent.title)
                    && newOffer.getPartnerContent().title != null) {
                this.partnerContent.title
                        = newOffer.getPartnerContent().title;
                updateFields.add(Field.PARTNER_CONTENT);
            }
        }

        return updateFields;
    }

    public String getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public int getStockCount() {
        return stockCount;
    }

    public PartnerContent getPartnerContent() {
        return partnerContent;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private class PartnerContent{
        @JsonProperty("description")
        public String description;
        @JsonProperty("title")
        public String title;

        public PartnerContent() {
        }

        public PartnerContent(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }
}

class SubscriberService {
    List<Field> triggers;
    List<Field> shipments;

    public SubscriberService(List<Field> triggers, List<Field> shipments) {
        this.triggers = triggers;
        this.shipments = shipments;
    }

    public void setTriggers(List<Field> triggers) {
        this.triggers = triggers;
    }

    public void setShipments(List<Field> shipments) {
        this.shipments = shipments;
    }

    public boolean isContainAtLeastOneTrigger(List<Field> inputTrigger){
        return inputTrigger.stream().anyMatch(x -> triggers.contains(x));
    }
}
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
class TracerOffer{
    @JsonProperty("trace_id")
    private String trace_id;
    @JsonProperty("offer")
    private Offer offer;

    public TracerOffer() {
    }

    public TracerOffer(String trace_id, Offer offer) {
        this.trace_id = trace_id;
        this.offer = offer;
    }

    @JsonGetter("offer")
    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }
}

class Service{
    private final List<SubscriberService> subscribers;
    private final List<Offer> offers;

    public Service(List<SubscriberService> subscribers) {
        this.subscribers = subscribers;
        this.offers = new LinkedList<>();
    }

    public void updateOffer(String input) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            TracerOffer tracerOffer = mapper.readValue(input, TracerOffer.class);
            Offer newOffer =  tracerOffer.getOffer();

            List<Field> updatedFields = new LinkedList<>();

            boolean contains = false;

            for (int i = 0; i < offers.size(); i++) {
                if(offers.get(i).getId().equals(newOffer.getId())){
                    updatedFields = offers.get(i).update(newOffer);
                    newOffer = offers.get(i);
                    contains = true;
                    break;
                }
            }

            if(updatedFields.isEmpty() && !contains){
                updatedFields = new Offer().update(newOffer);
                offers.add(newOffer);
            }

            List<Field> finalUpdatedFields = updatedFields;
            Offer finalNewOffer = newOffer;

            subscribers.stream().filter(subscriberService -> subscriberService.isContainAtLeastOneTrigger(finalUpdatedFields))
                    .forEach(subscriberService -> {

                        tracerOffer.setOffer(finalNewOffer);

                        ObjectNode object = mapper.valueToTree(tracerOffer);

                        subscriberService.shipments.forEach(shipment -> {
                            ((ObjectNode) object.get("offer")).remove(shipment.name().toLowerCase());
                        });

                        try {
                            System.out.println(mapper.writeValueAsString(object));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        catch (JsonProcessingException e){
            System.out.println(input);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int subcrNum = in.nextInt();
        int offersNum = in.nextInt();

        LinkedList<SubscriberService> subscriberServices = new LinkedList<>();

        for (int i = 0; i < subcrNum; i++) {
            int triggersNum = in.nextInt();
            int shipmentsNum = in.nextInt();

            LinkedList<Field> triggers = new LinkedList<>();
            for (int j = 0; j < triggersNum; j++) {
                triggers.add(Field.valueOf(in.next().toUpperCase()));
            }

            LinkedList<Field> shipments = new LinkedList<>(List.of(Field.PRICE, Field.PARTNER_CONTENT, Field.STOCK_COUNT));
            for (int j = 0; j < shipmentsNum; j++) {
                shipments.remove(Field.valueOf(in.next().toUpperCase()));
            }

            if(shipmentsNum == 0){
                shipments = new LinkedList<>();
            }

            subscriberServices.add(new SubscriberService(triggers, shipments));
        }

        Service service = new Service(subscriberServices);

        for (int i = 0; i < offersNum; i++) {
            String s = in.nextLine();
            if(s.equals("")){
                i--;
            }
            else {
                service.updateOffer(s);
            }
        }

        in.close();

    }
}