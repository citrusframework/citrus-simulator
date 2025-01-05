import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { IMessageHeader } from '../message-header.model';
import { sampleWithRequiredData, sampleWithPartialData, sampleWithFullData } from '../message-header.test-samples';

import { MessageHeaderService, RestMessageHeader } from './message-header.service';

const requireRestSample: RestMessageHeader = {
  ...sampleWithRequiredData,
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('MessageHeader Service', () => {
  let service: MessageHeaderService;
  let httpMock: HttpTestingController;
  let expectedResult: IMessageHeader | IMessageHeader[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MessageHeaderService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MessageHeader', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    describe('addMessageHeaderToCollectionIfMissing', () => {
      it('should add a MessageHeader to an empty array', () => {
        const messageHeader: IMessageHeader = sampleWithRequiredData;
        expectedResult = service.addMessageHeaderToCollectionIfMissing([], messageHeader);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(messageHeader);
      });

      it('should not add a MessageHeader to an array that contains it', () => {
        const messageHeader: IMessageHeader = sampleWithRequiredData;
        const messageHeaderCollection: IMessageHeader[] = [
          {
            ...messageHeader,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMessageHeaderToCollectionIfMissing(messageHeaderCollection, messageHeader);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MessageHeader to an array that doesn't contain it", () => {
        const messageHeader: IMessageHeader = sampleWithRequiredData;
        const messageHeaderCollection: IMessageHeader[] = [sampleWithPartialData];
        expectedResult = service.addMessageHeaderToCollectionIfMissing(messageHeaderCollection, messageHeader);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(messageHeader);
      });

      it('should add only unique MessageHeader to an array', () => {
        const messageHeaderArray: IMessageHeader[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const messageHeaderCollection: IMessageHeader[] = [sampleWithRequiredData];
        expectedResult = service.addMessageHeaderToCollectionIfMissing(messageHeaderCollection, ...messageHeaderArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const messageHeader: IMessageHeader = sampleWithRequiredData;
        const messageHeader2: IMessageHeader = sampleWithPartialData;
        expectedResult = service.addMessageHeaderToCollectionIfMissing([], messageHeader, messageHeader2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(messageHeader);
        expect(expectedResult).toContain(messageHeader2);
      });

      it('should accept null and undefined values', () => {
        const messageHeader: IMessageHeader = sampleWithRequiredData;
        expectedResult = service.addMessageHeaderToCollectionIfMissing([], null, messageHeader, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(messageHeader);
      });

      it('should return initial array if no MessageHeader is added', () => {
        const messageHeaderCollection: IMessageHeader[] = [sampleWithRequiredData];
        expectedResult = service.addMessageHeaderToCollectionIfMissing(messageHeaderCollection, undefined, null);
        expect(expectedResult).toEqual(messageHeaderCollection);
      });
    });

    describe('compareMessageHeader', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMessageHeader(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { headerId: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMessageHeader(entity1, entity2);
        const compareResult2 = service.compareMessageHeader(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { headerId: 123 };
        const entity2 = { headerId: 456 };

        const compareResult1 = service.compareMessageHeader(entity1, entity2);
        const compareResult2 = service.compareMessageHeader(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { headerId: 123 };
        const entity2 = { headerId: 123 };

        const compareResult1 = service.compareMessageHeader(entity1, entity2);
        const compareResult2 = service.compareMessageHeader(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
