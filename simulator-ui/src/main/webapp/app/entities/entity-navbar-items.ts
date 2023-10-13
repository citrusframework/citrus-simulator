import NavbarItem from 'app/layouts/navbar/navbar-item.model';

export const EntityNavbarItems: NavbarItem[] = [
  {
    name: 'Message',
    route: '/message',
    translationKey: 'global.menu.entities.message',
  },
  {
    name: 'TestParameter',
    route: '/test-parameter',
    translationKey: 'global.menu.entities.testParameter',
  },
  {
    name: 'TestResult',
    route: '/test-result',
    translationKey: 'global.menu.entities.testResult',
  },
];
