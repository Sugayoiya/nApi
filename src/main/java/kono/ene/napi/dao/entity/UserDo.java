package kono.ene.napi.dao.entity;

import kono.ene.napi.dao.base.BaseDo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Document("nintendo_user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDo extends BaseDo {
    @Id
    private String id;
    @Field("qid")
    private Long qid;

    @Field("nickname")
    private String nickname;
    @Field("screenName")
    private String screenName;

    @Field("birthday")
    private String birthday;
    @Field("language")
    private String language;
    @Field("country")

    private String country;
    @Field("gender")
    private String gender;
    @Field("isChild")
    private Boolean isChild;

    @Field("analyticsOptedIn")
    private Boolean analyticsOptedIn;
    @Field("analyticsOptedInUpdatedAt")
    private Integer analyticsOptedInUpdatedAt;
    // TODO figure out structure
    @Field("candidateMiis")
    private List<?> candidateMiis;
    @Field("clientFriendsOptedIn")
    private Boolean clientFriendsOptedIn;
    @Field("clientFriendsOptedInUpdatedAt")
    private Integer clientFriendsOptedInUpdatedAt;

    @Field("emailOptedIn")
    private Boolean emailOptedIn;
    @Field("emailOptedInUpdatedAt")
    private Integer emailOptedInUpdatedAt;
    @Field("emailVerified")
    private Boolean emailVerified;

    @Field("createdAt")
    private Integer createdAt;
    @Field("updatedAt")
    private Integer updatedAt;


    @Field("analyticsPermissions")
    private AnalyticsPermissionsDTO analyticsPermissions;

    @NoArgsConstructor
    @Data
    public static class AnalyticsPermissionsDTO {
        @Field("internalAnalysis")
        private InternalAnalysisDTO internalAnalysis;
        @Field("targetMarketing")
        private TargetMarketingDTO targetMarketing;

        @NoArgsConstructor
        @Data
        public static class InternalAnalysisDTO {
            @Field("updatedAt")
            private Integer updatedAt;
            @Field("permitted")
            private Boolean permitted;
        }

        @NoArgsConstructor
        @Data
        public static class TargetMarketingDTO {
            @Field("permitted")
            private Boolean permitted;
            @Field("updatedAt")
            private Integer updatedAt;
        }
    }

    @Field("eachEmailOptedIn")
    private EachEmailOptedInDTO eachEmailOptedIn;

    @NoArgsConstructor
    @Data
    public static class EachEmailOptedInDTO {
        @Field("deals")
        private DealsDTO deals;
        @Field("survey")
        private SurveyDTO survey;

        @NoArgsConstructor
        @Data
        public static class DealsDTO {
            @Field("updatedAt")
            private Integer updatedAt;
            @Field("optedIn")
            private Boolean optedIn;
        }

        @NoArgsConstructor
        @Data
        public static class SurveyDTO {
            @Field("optedIn")
            private Boolean optedIn;
            @Field("updatedAt")
            private Integer updatedAt;
        }
    }

    @Field("timezone")
    private TimezoneDTO timezone;

    @NoArgsConstructor
    @Data
    public static class TimezoneDTO {
        @Field("utcOffsetSeconds")
        private Integer utcOffsetSeconds;
        @Field("id")
        private String id;
        @Field("name")
        private String name;
        @Field("utcOffset")
        private String utcOffset;
    }
}


