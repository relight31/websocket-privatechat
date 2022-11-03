import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  title = 'chatfrontend';
  form!: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) {}
  ngOnInit(): void {
    this.createForm();
  }

  createForm() {
    this.form = this.fb.group({
      sender: this.fb.control(0, [Validators.required]),
      recipient: this.fb.control(0, [Validators.required]),
    });
  }

  submitForm() {
    const id1 = Number.parseInt(this.form.value.sender);
    const id2 = Number.parseInt(this.form.value.recipient);
    const channelName = Math.min(id1, id2) + 'and' + Math.max(id1, id2);
    console.log('>>>> channel name: ' + channelName);
    this.router.navigate(['/chat', channelName], {
      queryParams: { sender: id1 },
    });
  }
}
