import pyshark
import pandas as pd
from collections import defaultdict

# Define the columns order based on your requested format
columns = [
    'duration', 'protocol_type', 'service', 'flag', 'src_bytes', 'dst_bytes',
    'wrong_fragment', 'hot', 'logged_in', 'num_compromised',
    'count', 'srv_count', 'serror_rate', 'srv_serror_rate', 'rerror_rate'
]

def extract_features_from_pcap(pcap_file, output_file):
    cap = pyshark.FileCapture(pcap_file, display_filter="ip or tcp or udp or icmp")
    features = []
    session_data = defaultdict(lambda: {'count': 0, 'srv_count': 0, 'serror_rate': 0, 'srv_serror_rate': 0, 'rerror_rate': 0})
    
    for packet in cap:
        try:
            # Basic features
            duration = float(packet.sniff_time.timestamp())
            protocol_type = packet.transport_layer if hasattr(packet, 'transport_layer') else 'tcp'
            service = packet.highest_layer if hasattr(packet, 'highest_layer') else 'other'
            flag = packet.tcp.flags if hasattr(packet, 'tcp') else 'SF'
            
            src_bytes = int(packet.length) if hasattr(packet, 'length') else 0
            dst_bytes = int(packet.ip.len) if hasattr(packet, 'ip') else 0
            
            wrong_fragment = int(packet.ip.frag_offset) if hasattr(packet, 'ip.frag_offset') else 0
            hot = int(packet.tcp.urgent_pointer) if hasattr(packet, 'tcp.urgent_pointer') else 0
            logged_in = 1 if hasattr(packet, 'tcp') and 'SYN' in flag else 0
            num_compromised = 0  # Not directly extractable
            
            src_ip = packet.ip.src if hasattr(packet, 'ip') else '0.0.0.0'
            dst_ip = packet.ip.dst if hasattr(packet, 'ip') else '0.0.0.0'
            session_key = f"{src_ip}-{dst_ip}"
            
            session_data[session_key]['count'] += 1
            session_data[dst_ip]['srv_count'] += 1
            
            serror_rate = int('S' in flag) / session_data[session_key]['count'] if session_data[session_key]['count'] > 0 else 0
            srv_serror_rate = int('S' in flag) / session_data[dst_ip]['srv_count'] if session_data[dst_ip]['srv_count'] > 0 else 0
            rerror_rate = int('R' in flag) / session_data[session_key]['count'] if session_data[session_key]['count'] > 0 else 0
            
            # Append row in requested format
            row = [
                duration, protocol_type, service, flag, src_bytes, dst_bytes,
                wrong_fragment, hot, logged_in, num_compromised,
                session_data[session_key]['count'], session_data[dst_ip]['srv_count'],
                serror_rate, srv_serror_rate, rerror_rate
            ]
            features.append(row)
        
        except AttributeError:
            continue
    
    cap.close()
    
    # Save output as a text file in requested format
    with open(output_file, 'w') as f:
        for row in features:
            f.write(",".join(map(str, row)) + "\n")

# Example Usage
pcap_file = "f2.pcapng"
output_file = "output.txt"
extract_features_from_pcap(pcap_file, output_file)
print(f"Extracted features saved to {output_file}")

