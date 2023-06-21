package kono.ene.napi.response.splat3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BattleHistoryResponse {
    @JsonProperty("latestBattleHistories")
    private LatestBattleHistoriesDTO latestBattleHistories;
    // TODO
    @JsonProperty("currentFest")
    private Object currentFest;

    @NoArgsConstructor
    @Data
    public static class LatestBattleHistoriesDTO {
        @JsonProperty("summary")
        private SummaryDTO summary;
        @JsonProperty("historyGroupsOnlyFirst")
        private HistoryGroupsOnlyFirstDTO historyGroupsOnlyFirst;
        @JsonProperty("historyGroups")
        private HistoryGroupsDTO historyGroups;

        @NoArgsConstructor
        @Data
        public static class SummaryDTO {
            @JsonProperty("assistAverage")
            private Double assistAverage;
            @JsonProperty("deathAverage")
            private Double deathAverage;
            @JsonProperty("killAverage")
            private Double killAverage;
            @JsonProperty("perUnitTimeMinute")
            private Integer perUnitTimeMinute;
            @JsonProperty("specialAverage")
            private Double specialAverage;
            @JsonProperty("win")
            private Integer win;
            @JsonProperty("lose")
            private Integer lose;
        }

        @NoArgsConstructor
        @Data
        public static class HistoryGroupsOnlyFirstDTO {
            @JsonProperty("nodes")
            private List<OnlyFirstNodesDTO> nodes;

            @NoArgsConstructor
            @Data
            public static class OnlyFirstNodesDTO {
                @JsonProperty("historyDetails")
                private HistoryDetailsDTO historyDetails;

                @NoArgsConstructor
                @Data
                public static class HistoryDetailsDTO {
                    @JsonProperty("nodes")
                    private List<OnlyFirstDetailNodesDTO> nodes;

                    @NoArgsConstructor
                    @Data
                    public static class OnlyFirstDetailNodesDTO {
                        @JsonProperty("player")
                        private PlayerDTO player;
                        @JsonProperty("id")
                        private String id;

                        @NoArgsConstructor
                        @Data
                        public static class PlayerDTO {
                            @JsonProperty("weapon")
                            private WeaponDTO weapon;
                            @JsonProperty("id")
                            private String id;

                            @NoArgsConstructor
                            @Data
                            public static class WeaponDTO {
                                @JsonProperty("specialWeapon")
                                private SpecialWeaponDTO specialWeapon;
                                @JsonProperty("id")
                                private String id;

                                @NoArgsConstructor
                                @Data
                                public static class SpecialWeaponDTO {
                                    @JsonProperty("maskingImage")
                                    private MaskingImageDTO maskingImage;
                                    @JsonProperty("id")
                                    private String id;

                                    @NoArgsConstructor
                                    @Data
                                    public static class MaskingImageDTO {
                                        @JsonProperty("width")
                                        private Integer width;
                                        @JsonProperty("height")
                                        private Integer height;
                                        @JsonProperty("maskImageUrl")
                                        private String maskImageUrl;
                                        @JsonProperty("overlayImageUrl")
                                        private String overlayImageUrl;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @NoArgsConstructor
        @Data
        public static class HistoryGroupsDTO {
            @JsonProperty("nodes")
            private List<NodesDTO> nodes;

            @NoArgsConstructor
            @Data
            public static class NodesDTO {
                @JsonProperty("historyDetails")
                private HistoryDetailsDTO historyDetails;

                @NoArgsConstructor
                @Data
                public static class HistoryDetailsDTO {
                    @JsonProperty("nodes")
                    private List<DetailNodesDTO> nodes;

                    @NoArgsConstructor
                    @Data
                    public static class DetailNodesDTO {
                        @JsonProperty("id")
                        private String id;
                        @JsonProperty("vsMode")
                        private VsModeDTO vsMode;
                        @JsonProperty("vsRule")
                        private VsRuleDTO vsRule;
                        @JsonProperty("vsStage")
                        private VsStageDTO vsStage;
                        @JsonProperty("judgement")
                        private String judgement;
                        @JsonProperty("player")
                        private PlayerDTO player;
                        @JsonProperty("knockout")
                        private String knockout;
                        @JsonProperty("myTeam")
                        private MyTeamDTO myTeam;
                        @JsonProperty("udemae")
                        private Object udemae;
                        @JsonProperty("bankaraMatch")
                        private Object bankaraMatch;
                        @JsonProperty("nextHistoryDetail")
                        private Object nextHistoryDetail;
                        @JsonProperty("previousHistoryDetail")
                        private PreviousHistoryDetailDTO previousHistoryDetail;

                        @NoArgsConstructor
                        @Data
                        public static class VsModeDTO {
                            @JsonProperty("mode")
                            private String mode;
                            @JsonProperty("id")
                            private String id;
                        }

                        @NoArgsConstructor
                        @Data
                        public static class VsRuleDTO {
                            @JsonProperty("name")
                            private String name;
                            @JsonProperty("id")
                            private String id;
                        }

                        @NoArgsConstructor
                        @Data
                        public static class VsStageDTO {
                            @JsonProperty("name")
                            private String name;
                            @JsonProperty("id")
                            private String id;
                            @JsonProperty("image")
                            private ImageDTO image;

                            @NoArgsConstructor
                            @Data
                            public static class ImageDTO {
                                @JsonProperty("url")
                                private String url;
                            }
                        }

                        @NoArgsConstructor
                        @Data
                        public static class PlayerDTO {
                            @JsonProperty("weapon")
                            private WeaponDTO weapon;
                            @JsonProperty("id")
                            private String id;
                            @JsonProperty("festGrade")
                            private String festGrade;

                            @NoArgsConstructor
                            @Data
                            public static class WeaponDTO {
                                @JsonProperty("name")
                                private String name;
                                @JsonProperty("image")
                                private ImageDTO image;
                                @JsonProperty("id")
                                private String id;

                                @NoArgsConstructor
                                @Data
                                public static class ImageDTO {
                                    @JsonProperty("url")
                                    private String url;
                                }
                            }
                        }

                        @NoArgsConstructor
                        @Data
                        public static class MyTeamDTO {
                            @JsonProperty("result")
                            private ResultDTO result;

                            @NoArgsConstructor
                            @Data
                            public static class ResultDTO {
                                @JsonProperty("paintPoint")
                                private Integer paintPoint;
                                @JsonProperty("score")
                                private Object score;
                            }
                        }

                        @NoArgsConstructor
                        @Data
                        public static class PreviousHistoryDetailDTO {
                            @JsonProperty("id")
                            private String id;
                        }
                    }
                }
            }
        }
    }
}
