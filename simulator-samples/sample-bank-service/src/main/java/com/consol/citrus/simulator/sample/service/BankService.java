/*
 * Copyright 2006-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.simulator.sample.service;

import com.consol.citrus.simulator.exception.SimulatorException;
import com.consol.citrus.simulator.sample.model.Bank;
import com.consol.citrus.simulator.sample.model.BankAccount;
import com.consol.citrus.simulator.sample.model.CalculateIbanResponse;
import com.consol.citrus.simulator.sample.model.ValidateIbanResponse;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BankService {
    private final Pattern ibanPattern = Pattern.compile("DE([0-9]{2})([0-9]{8})([0-9]{10})");

    public ValidateIbanResponse validate(String iban) {
        final boolean validIban = validateIban(iban);
        final String[] parsedIban = parseIban(iban);
        final String parsedSortCode = parsedIban[1];
        final String parsedAccountNumber = parsedIban[2];
        final Bank bank = lookupBank(parsedSortCode);

        return ValidateIbanResponse.builder()
                .bankAccount(BankAccount.builder()
                        .accountNumber(parsedAccountNumber)
                        .sortCode(bank.sortCode())
                        .bank(bank.longName())
                        .bic(bank.bic())
                        .iban(iban)
                        .build()
                )
                .valid(validIban)
                .build();
    }

    public CalculateIbanResponse calculate(String sortCode, String bankAccount) {
        final String calculatedIban = calculateIban(sortCode, bankAccount);
        final String[] parsedIban = parseIban(calculatedIban);
        final String parsedSortCode = parsedIban[1];
        final String parsedAccountNumber = parsedIban[2];
        final Bank bank = lookupBank(parsedSortCode);

        return CalculateIbanResponse.builder()
                .bankAccount(BankAccount.builder()
                        .accountNumber(parsedAccountNumber)
                        .sortCode(bank.sortCode())
                        .bank(bank.longName())
                        .bic(bank.bic())
                        .iban(calculatedIban)
                        .build()
                )
                .build();
    }

    private Bank lookupBank(String sortCode) {
        return Bank.bySortCode(sortCode).orElse(Bank.UNKNOWN);
    }

    private String calculateIban(String bankCode, String accountNumber) {
        try {
            String ibanWithoutCheckCode = createIban("00", bankCode, accountNumber);
            String checkSum = new IBANCheckDigit().calculate(ibanWithoutCheckCode);
            return createIban(checkSum, bankCode, accountNumber);
        } catch (CheckDigitException e) {
            throw new SimulatorException(String.format("Error calculating IBAN [code:%s,account:%s]", bankCode, accountNumber));
        }
    }

    private String createIban(String checkSum, String sortCode, String accountNumber) {
        return String.format("DE%02d%08d%010d", Integer.parseInt(checkSum), Integer.parseInt(sortCode), Integer.parseInt(accountNumber));
    }

    private boolean validateIban(String iban) {
        return new IBANCheckDigit().isValid(iban);
    }

    /**
     * Extracts the checksum, bank code and account number from the supplied IBAN.
     *
     * @param iban an iban in the format DEkkbbbbbbbbcccccccccc (k=checksum, b=bank code, c=account number)
     * @return an array [checksum, bank code,account number] or null, if the iban could not be parsed
     */
    private String[] parseIban(String iban) {
        Matcher m = ibanPattern.matcher(iban);
        if (m.matches()) {
            return new String[]{
                    m.group(1),
                    m.group(2),
                    m.group(3)
            };
        }
        return new String[]{"", "", ""};
    }
}
