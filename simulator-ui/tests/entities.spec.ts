import {test} from "@playwright/test";
import {mockBackendResponse} from "./helper-functions";


const entityPageContentMap = [
  {
    apiUrl: '...' /*TODO:die URLs der entsprechenden endpoints einfügen*/,
    entityUrl: 'http://localhost:9000/message',
    contentJson: {/*TODO: den Inhalt der Jsons befüllen entsprechend was gesendet wird*/}
  },
  {apiUrl: '...', entityUrl: 'http://localhost:9000/message-header', contentJson: {}},
  {apiUrl: '...', entityUrl: 'http://localhost:9000/scenario-execution', contentJson: {}},
  {apiUrl: '...', entityUrl: 'http://localhost:9000/scenario-action', contentJson: {}},
  {apiUrl: '...', entityUrl: 'http://localhost:9000/scenario-parameter', contentJson: {}},
  {apiUrl: '...', entityUrl: 'http://localhost:9000/test-result', contentJson: {}},
  {apiUrl: '...', entityUrl: 'http://localhost:9000/test-parameter', contentJson: {}}
]

test('should test if content is displayed for every entity page', async ({page}) => {
  for (const entity of entityPageContentMap) {
    await mockBackendResponse(page, entity.apiUrl, entity.contentJson)
    await page.goto(entity.entityUrl);

    //TODO: Inhalt der Tabellen prüfen --> evnetuell Map ergänzen mit Testselektoren.
  }
})
