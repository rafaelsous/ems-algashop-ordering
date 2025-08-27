package com.rafaelsousa.algashop.ordering.infrastructure.utility.modelmapper;

import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerOutput;
import com.rafaelsousa.algashop.ordering.application.utility.Mapper;
import com.rafaelsousa.algashop.ordering.domain.model.commons.FullName;
import com.rafaelsousa.algashop.ordering.domain.model.customer.BirthDate;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Objects;

@Configuration
public class ModelMapperConfig {

    private static final Converter<FullName, String> fullNameToFirstNameConverter = mappingContext -> {
        FullName fullName = mappingContext.getSource();

        if (Objects.isNull(fullName)) return null;

        return fullName.firstName();
    };

    private static final Converter<FullName, String> fullNameToLastNameConverter = mappingContext -> {
        FullName fullName = mappingContext.getSource();

        if (Objects.isNull(fullName)) return null;

        return fullName.lastName();
    };

    private static final Converter<BirthDate, LocalDate> birthDateToLocalDateConverter = mappingContext -> {
        BirthDate birthDate = mappingContext.getSource();

        if (Objects.isNull(birthDate)) return null;

        return birthDate.value();
    };

    @Bean
    public Mapper mapper() {
        ModelMapper modelMapper = new ModelMapper();
        configuration(modelMapper);

        return modelMapper::map;
    }

    private void configuration(ModelMapper modelMapper) {
        modelMapper.getConfiguration()
                .setSourceNamingConvention(NamingConventions.NONE)
                .setDestinationNamingConvention(NamingConventions.NONE)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Customer.class, CustomerOutput.class)
                .addMappings(mapping -> {
                    mapping.using(fullNameToFirstNameConverter).map(Customer::fullName, CustomerOutput::setFirstName);
                    mapping.using(fullNameToLastNameConverter).map(Customer::fullName, CustomerOutput::setLastName);
                    mapping.using(birthDateToLocalDateConverter).map(Customer::birthDate, CustomerOutput::setBirthDate);
                });
    }
}