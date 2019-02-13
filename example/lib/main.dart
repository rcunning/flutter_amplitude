import 'package:flutter/material.dart';
import 'package:flutter_amplitude/flutter_amplitude.dart';

void main() {
  runApp(new MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  initState() {
    super.initState();
    init();
  }

  init() async {
    FlutterAmplitude.init("your api key", true, true, false);
    FlutterAmplitude.addGeneralProperties({'generalParameterKey': 'generalParameterValue'});
    FlutterAmplitude.setUserId("info@sample.com");
    FlutterAmplitude.setUserProperties({'userParameterKey': 'userParameterValue'});
    FlutterAmplitude.setUserPropertiesOnce({'userParameterKey2': 'userParameterValue2'});
    FlutterAmplitude.logEvent("EVENT_KEY", {
      'eventParameterKey': 'eventParameterValue',
      'eventParameterInt': 5,
      'eventParameterDate': {'a': 'b'}
    });

    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('Plugin example app'),
        ),
      ),
    );
  }
}
