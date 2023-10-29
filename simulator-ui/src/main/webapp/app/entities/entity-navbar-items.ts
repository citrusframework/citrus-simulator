import NavbarItem from 'app/layouts/navbar/navbar-item.model';

export const EntityNavbarItems: NavbarItem[] = [
  {
    name: 'Message',
    route: '/message',
    translationKey: 'global.menu.entities.message',
  },
  {
    name: 'MessageHeader',
    route: '/message-header',
    translationKey: 'global.menu.entities.messageHeader',
  },
  {
    name: 'ScenarioExecution',
    route: 'scenario-execution',
    translationKey: 'global.menu.entities.scenarioExecution',
  },
  {
    name: 'ScenarioAction',
    route: 'scenario-action',
    translationKey: 'global.menu.entities.scenarioAction',
  },
  {
    name: 'ScenarioParameter',
    route: 'scenario-parameter',
    translationKey: 'global.menu.entities.scenarioParameter',
  },
  {
    name: 'TestResult',
    route: '/test-result',
    translationKey: 'global.menu.entities.testResult',
  },
  {
    name: 'TestParameter',
    route: '/test-parameter',
    translationKey: 'global.menu.entities.testParameter',
  },
];
