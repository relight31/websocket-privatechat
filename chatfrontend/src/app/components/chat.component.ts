import { Component, OnInit } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { firstValueFrom, Observable, timestamp } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageEntity } from '../models';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css'],
})
export class ChatComponent implements OnInit {
  url: string = 'http://localhost:8080';
  channelName!: string;
  sender!: string;
  messages: MessageEntity[] = [];
  socket?: WebSocket;
  stompClient?: Stomp.Client;
  messageForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.channelName = this.route.snapshot.params['chatId'];
    this.route.queryParams.subscribe((params) => {
      this.sender = params['sender'];
    });
    this.createForm();
    this.connectToChat();
  }

  connectToChat() {
    console.log('>>>> loading saved chat history');
    this.loadChatHistory(this.channelName);

    console.log('>>>> connecting to chat channel: ' + this.channelName);
    this.socket = new SockJS(this.url + '/chat'); //chat endpoint
    this.stompClient = Stomp.over(this.socket);
    this.stompClient.connect({}, (frame) => {
      console.log('>>>> connected to: ' + frame);
      //subscribe to channel
      this.stompClient!.subscribe(
        '/topic/messages/' + this.channelName,
        (response) => {
          // what to do when client receives data (messages)
          console.log(response.body);
          this.messages.push(JSON.parse(response.body));
        }
      );
    });
  }

  loadChatHistory(chatId: string) {
    // call get method to backend to get saved messages
    const params = new HttpParams();
    params.append('chatId', chatId);

    firstValueFrom(
      this.http.get<MessageEntity[]>(this.url + '/getMessages', {
        params: params,
      })
    ).then((result) => {
      this.messages = result;
    });
  }

  createForm() {
    this.messageForm = this.fb.group({
      message: this.fb.control('', [Validators.required]),
    });
  }

  sendMessage() {
    if (this.messageForm.value.message != '') {
      this.stompClient?.send(
        '/app/chat/' + this.channelName,
        {},
        JSON.stringify({
          chatId: this.channelName,
          sender: this.sender,
          content: this.messageForm.value.message,
          timestamp: 'server defined',
        })
      );
    }
    this.createForm();
  }
}
