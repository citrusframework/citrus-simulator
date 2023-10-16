import { Component } from '@angular/core';

import { faGithub } from '@fortawesome/free-brands-svg-icons';

import SharedModule from 'app/shared/shared.module';

@Component({
  standalone: true,
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
  imports: [SharedModule],
})
export default class FooterComponent {
  protected readonly faGithub = faGithub;
}
