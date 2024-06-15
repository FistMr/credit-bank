package com.puchkov.deal.mapper;

import com.puchkov.deal.dto.FinishRegistrationRequestDto;
import com.puchkov.deal.entity.Employment;
import org.springframework.stereotype.Component;

@Component
public class EmploymentMapper {

    public Employment dtoToEntity(FinishRegistrationRequestDto finishRegistrationRequestDto){
        return Employment.builder()
                .status(finishRegistrationRequestDto.getEmployment().getEmploymentStatus())
                .employerINN(finishRegistrationRequestDto.getEmployment().getEmployerINN())
                .salary(finishRegistrationRequestDto.getEmployment().getSalary())
                .position(finishRegistrationRequestDto.getEmployment().getPosition())
                .workExperienceTotal(finishRegistrationRequestDto.getEmployment().getWorkExperienceTotal())
                .workExperienceCurrent(finishRegistrationRequestDto.getEmployment().getWorkExperienceCurrent())
                .build();
    }
}
