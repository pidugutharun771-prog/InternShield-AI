package com.internshield.controller;
import com.internshield.dto.*; import com.internshield.service.RiskAnalysisService; import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/analysis") @CrossOrigin(origins={"http://localhost:5173","https://internshield-frontend-ygy9.onrender.com","https://intern-shield-ai-five.vercel.app"})
public class AnalysisController {
 private final RiskAnalysisService service; public AnalysisController(RiskAnalysisService service){this.service=service;}
 @PostMapping public AnalysisResponse analyze(@Valid @RequestBody AnalyzeRequest request){return service.analyze(request);}
 @GetMapping("/recent") public List<AnalysisResponse> recent(){return service.recent();}
}
