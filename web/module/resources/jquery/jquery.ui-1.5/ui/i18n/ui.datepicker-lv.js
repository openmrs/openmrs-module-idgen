/**
 * @author Arturas Paleicikas <arturas.paleicikas@metasite.net>
 */
jQuery(function($){
	$.datepicker.regional['lv'] = {
		clearText: 'Not????r????t', clearStatus: '',
		closeText: 'Aizv????rt', closeStatus: '',
		prevText: 'Iepr',  prevStatus: '',
		nextText: 'N????ka', nextStatus: '',
		currentText: '????odien', currentStatus: '',
		monthNames: ['Janv????ris','Febru????ris','Marts','Apr????lis','Maijs','J????nijs',
		'J????lijs','Augusts','Septembris','Oktobris','Novembris','Decembris'],
		monthNamesShort: ['Jan','Feb','Mar','Apr','Mai','J????n',
		'J????l','Aug','Sep','Okt','Nov','Dec'],
		monthStatus: '', yearStatus: '',
		weekHeader: 'Nav', weekStatus: '',
		dayNames: ['sv????tdiena','pirmdiena','otrdiena','tre????diena','ceturtdiena','piektdiena','sestdiena'],
		dayNamesShort: ['svt','prm','otr','tre','ctr','pkt','sst'],
		dayNamesMin: ['Sv','Pr','Ot','Tr','Ct','Pk','Ss'],
		dayStatus: 'DD', dateStatus: 'D, M d',
		dateFormat: 'dd-mm-yy', firstDay: 1, 
		initStatus: '', isRTL: false};
	$.datepicker.setDefaults($.datepicker.regional['lv']);
});
