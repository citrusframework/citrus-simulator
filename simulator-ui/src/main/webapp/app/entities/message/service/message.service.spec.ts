import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { IMessage } from '../message.model';
import { sampleWithRequiredData, sampleWithPartialData, sampleWithFullData } from '../message.test-samples';

import { MessageService, RestMessage } from './message.service';

const requireRestSample: RestMessage = {
  ...sampleWithRequiredData,
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('Message Service', () => {
  let service: MessageService;
  let httpMock: HttpTestingController;
  let expectedResult: IMessage | IMessage[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MessageService);
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

    it('should return a list of Message', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    describe('addMessageToCollectionIfMissing', () => {
      it('should add a Message to an empty array', () => {
        const message: IMessage = sampleWithRequiredData;
        expectedResult = service.addMessageToCollectionIfMissing([], message);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(message);
      });

      it('should not add a Message to an array that contains it', () => {
        const message: IMessage = sampleWithRequiredData;
        const messageCollection: IMessage[] = [
          {
            ...message,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMessageToCollectionIfMissing(messageCollection, message);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Message to an array that doesn't contain it", () => {
        const message: IMessage = sampleWithRequiredData;
        const messageCollection: IMessage[] = [sampleWithPartialData];
        expectedResult = service.addMessageToCollectionIfMissing(messageCollection, message);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(message);
      });

      it('should add only unique Message to an array', () => {
        const messageArray: IMessage[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const messageCollection: IMessage[] = [sampleWithRequiredData];
        expectedResult = service.addMessageToCollectionIfMissing(messageCollection, ...messageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const message: IMessage = sampleWithRequiredData;
        const message2: IMessage = sampleWithPartialData;
        expectedResult = service.addMessageToCollectionIfMissing([], message, message2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(message);
        expect(expectedResult).toContain(message2);
      });

      it('should accept null and undefined values', () => {
        const message: IMessage = sampleWithRequiredData;
        expectedResult = service.addMessageToCollectionIfMissing([], null, message, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(message);
      });

      it('should return initial array if no Message is added', () => {
        const messageCollection: IMessage[] = [sampleWithRequiredData];
        expectedResult = service.addMessageToCollectionIfMissing(messageCollection, undefined, null);
        expect(expectedResult).toEqual(messageCollection);
      });
    });

    describe('compareMessage', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMessage(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { messageId: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMessage(entity1, entity2);
        const compareResult2 = service.compareMessage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { messageId: 123 };
        const entity2 = { messageId: 456 };

        const compareResult1 = service.compareMessage(entity1, entity2);
        const compareResult2 = service.compareMessage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { messageId: 123 };
        const entity2 = { messageId: 123 };

        const compareResult1 = service.compareMessage(entity1, entity2);
        const compareResult2 = service.compareMessage(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
