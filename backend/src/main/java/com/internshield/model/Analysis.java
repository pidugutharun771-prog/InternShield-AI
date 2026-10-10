package com.internshield.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter @NoArgsConstructor
public class Analysis {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(columnDefinition="TEXT", nullable=false) private String content;
 private int score;
 @Enumerated(EnumType.STRING) private RiskLevel riskLevel;
 private String emailDomain;
 private boolean freeEmail;
 private LocalDateTime createdAt;
 @ElementCollection(fetch=FetchType.EAGER) private List<String> indicators = new ArrayList<>();
 @ElementCollection(fetch=FetchType.EAGER) private List<String> recommendations = new ArrayList<>();
 @PrePersist void created(){ createdAt=LocalDateTime.now(); }
}
